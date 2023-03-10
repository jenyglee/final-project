package com.project.sparta.user.service;

import com.project.sparta.admin.dto.UserGradeDto;
import com.project.sparta.admin.dto.UserStatusDto;
import com.project.sparta.hashtag.dto.HashtagResponseDto;
import com.project.sparta.recommendCourse.repository.RecommendCourseBoardRepository;
import com.project.sparta.user.entity.StatusEnum;
import com.project.sparta.common.dto.PageResponseDto;
import com.project.sparta.communityBoard.repository.BoardRepository;
import com.project.sparta.exception.CustomException;
import com.project.sparta.hashtag.entity.Hashtag;
import com.project.sparta.hashtag.repository.HashtagRepository;
import com.project.sparta.security.dto.RegenerateTokenDto;
import com.project.sparta.security.dto.TokenDto;
import com.project.sparta.security.jwt.JwtUtil;
import com.project.sparta.user.dto.*;
import com.project.sparta.user.entity.User;
import com.project.sparta.user.entity.UserGradeEnum;
import com.project.sparta.user.entity.UserTag;
import com.project.sparta.user.repository.UserRepository;
import com.project.sparta.user.repository.UserTagRepository;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.project.sparta.exception.api.Status.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashtagRepository hashtagRepository;
    private final UserTagRepository userTagRepository;
    private final BoardRepository boardRepository;
    private final RecommendCourseBoardRepository recommendRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
    private final JavaMailSender javaMailSender;
    
    //????????????
    @Override
    public void signup(UserSignupDto signupDto) {
        // 1. User??? ?????? ??? ??????
        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());
        User user = User.userBuilder()
            .email(signupDto.getEmail())
            .password(encodedPassword)
            .nickName(signupDto.getNickName())
            .age(signupDto.getAge())
            .phoneNumber(signupDto.getPhoneNumber())
            .userImageUrl(signupDto.getImageUrl())
            .build();
        userRepository.save(user);

        // 2. hashtag??? ID ???????????? ?????? Usertag??? ???????????? ??????
        List<Long> longList = signupDto.getTagList();
        List<UserTag> userTagList = new ArrayList<>();
        if (longList != null) {
            longList.stream().forEach(tagId -> {
                Hashtag hashtag = hashtagRepository.findById(tagId)
                    .orElseThrow(() -> new CustomException(NOT_FOUND_HASHTAG));
                UserTag userTag = new UserTag(user, hashtag);
                userTagRepository.save(userTag);
                userTagList.add(userTag);
            });
        }

        // 3. User??? ?????? ???????????? ??????
        user.updateUserTags(userTagList);
    }

    //?????????
    @Override
    public ResponseEntity<TokenDto> login(LoginRequestDto requestDto) {

        if (requestDto.getEmail().isBlank() || requestDto.getPassword().isBlank()){
            throw new CustomException(INVALID_CONTENT);
        }

        User user = userRepository.findByEmail(requestDto.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        //??????2: ??????????????? ????????? ??????
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(NOT_MATCH_PASSWORD);
        }

        // 1. access/refresh token ?????????
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getGradeEnum(),
            user.getNickName(), user.getUserImageUrl());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole());

        // 2. ????????? DTO??? token??? nickName ??????
        TokenDto tokenDto = new TokenDto(
            accessToken,
            refreshToken,
            user.getNickName()
        );

        // 3. ????????? ???????????? refresh token ??????
        redisTemplate.opsForValue().set(
            user.getEmail(),
            refreshToken,
            JwtUtil.REFRESH_TOKEN_TIME,
            TimeUnit.MILLISECONDS
        );

        // 4. ?????? ??? ????????? 'Authorization'??? access token?????? ??????
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAccessToken());
        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }


    //????????????
    @Transactional
    public void logout(TokenDto tokenRequestDto) {
        // 1. token??? ??????
        String resultToekn = tokenRequestDto.getAccessToken();
        jwtUtil.validateToken(resultToekn);

        // 2. ????????? ???????????? ?????? token??? ??????
        Authentication authentication = jwtUtil.getAuthenticationByAccessToken(resultToekn);
        if (redisTemplate.opsForValue().get(authentication.getName()) != null) {
            redisTemplate.delete(authentication.getName());
        }

        //3. ????????? ??????
        Long expiration = jwtUtil.getExpiration(resultToekn);
        redisTemplate.opsForValue()
            .set(tokenRequestDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // ????????? ????????????
    @Override
    public void validateEmail(ValidateEmailDto emailDto) {
        Optional<User> findUser = userRepository.findByEmail(emailDto.getEmail());
        if (findUser.isPresent()) {
            throw new CustomException(CONFLICT_EMAIL);
        }
    }

    // ????????? ????????????
    @Override
    public void validateNickName(ValidateNickNameDto nickNameDto) {
        Optional<User> findUser = userRepository.findByNickName(nickNameDto.getNickName());
        if (findUser.isPresent()) {
            throw new CustomException(CONFLICT_NICKNAME);
        }
    }

    // ??? ?????? ??????
    @Override
    public InfoResponseDto getMyInfo(User user) {
        // ?????? ??? ???????????? ??? ??????
        Long communityCount = boardRepository.countMyCommunity(user.getId());

        // ?????? ??? ???????????? ??? ??????
        Long recommendCount = recommendRepository.countByUserId(user.getId());

        // ?????? ????????? ?????? ???
        int enterCount = user.getEnterCount();

        // ?????? ?????? ?????? ???
        int makeCount = boardRepository.countMyChat(user.getId());

        // ?????? ????????????
        List<UserTag> userTags = userTagRepository.findAllByUser(user);
        List<HashtagResponseDto> hashtagList = new ArrayList<>();
        for (UserTag tag : userTags) {
            hashtagList.add(new HashtagResponseDto(tag.getTag().getId(), tag.getTag().getName()));
        }

        return InfoResponseDto.builder()
            .nickName(user.getNickName())
            .communityCount(communityCount)
            .recommendCount(recommendCount)
            .enterCount(enterCount)
            .makeCount(makeCount)
            .tagList(hashtagList)
            .userGradeEnum(user.getGradeEnum())
            .build();
    }

    // ?????? ?????????
    @Override
    public ResponseEntity<TokenDto> regenerateToken(RegenerateTokenDto tokenDto) {
        String changeToken = tokenDto.getRefreshToken();
        try {
            jwtUtil.validateRefreshToken(changeToken);
            Authentication authentication = jwtUtil.getAuthenticationByRefreshToken(changeToken);

            String refreshToken = redisTemplate.opsForValue().get(authentication.getName());

            if (!refreshToken.equals(changeToken)) {
                throw new CustomException(DISCORD_TOKEN);
            }

            User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

            String new_refresh_token = jwtUtil.generateRefreshToken(user.getEmail(),
                user.getRole());

            TokenDto new_tokenDto = new TokenDto(
                jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getGradeEnum(),
                    user.getNickName(), user.getUserImageUrl()),
                new_refresh_token,
                user.getNickName()
            );

            redisTemplate.opsForValue().set(
                authentication.getName(),
                new_refresh_token,
                JwtUtil.REFRESH_TOKEN_TIME,
                TimeUnit.MILLISECONDS
            );

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtUtil.AUTHORIZATION_HEADER, new_tokenDto.getAccessToken());
            return new ResponseEntity<>(new_tokenDto, httpHeaders, HttpStatus.OK);
        } catch (AuthenticationException e) {
            throw new CustomException(DISCORD_TOKEN);
        }
    }

    //?????? ??????
    @Override
    @Transactional
    public void upgrade(UpgradeRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        if (requestDto.getGrade().equals("MANIA")) {
            user.changeGrade(UserGradeEnum.MOUNTAIN_MANIA);
        } else if (requestDto.getGrade().equals("GOD")) {
            user.changeGrade(UserGradeEnum.MOUNTAIN_GOD);
        }
    }

    //(????????????) ?????? ????????????
    @Override
    public PageResponseDto<List<UserListResponseDto>> getUserList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> results = userRepository.findAll(pageRequest);

        List<User> userList = results.getContent();
        long totalElements = results.getTotalElements();

        List<UserListResponseDto> userResponseDtoList = new ArrayList<>();
        for (User user : userList) {
            UserListResponseDto userResponseDto = UserListResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .grade(user.getGradeEnum())
                .createdAt(user.getCreateAt())
                .status(user.getStatus())
                .build();
            userResponseDtoList.add(userResponseDto);
        }

        return new PageResponseDto<>(page, totalElements, userResponseDtoList);
    }

    //(????????????) ?????? ????????????
    @Override
    public UserOneResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        return UserOneResponseDto.builder()
            .id(user.getId())
            .nickName(user.getNickName())
            .age(user.getAge())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .userGradeEnum(user.getGradeEnum())
            .createdAt(user.getCreateAt())
            .modifiedAt(user.getModifiedAt())
            .profileImage(user.getUserImageUrl())
            .status(user.getStatus())
            .build();
    }

    //(????????????) ?????? ?????? ??????
    @Override
    @Transactional
    public void changeGrade(UserGradeDto gradeDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        if (gradeDto.getGrade() == 0) {
            user.changeGrade(UserGradeEnum.MOUNTAIN_CHILDREN);
        } else if (gradeDto.getGrade() == 1) {
            user.changeGrade(UserGradeEnum.MOUNTAIN_MANIA);
        } else if (gradeDto.getGrade() == 2) {
            user.changeGrade(UserGradeEnum.MOUNTAIN_GOD);
        }
    }

    //(????????????) ?????? ??????/??????
    @Override
    @Transactional
    public void changeStatus(UserStatusDto statusDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        if (statusDto.getStatus() == 0) {
            user.changeStatus(StatusEnum.USER_WITHDRAWAL);
        } else if (statusDto.getStatus() == 1) {
            user.changeStatus(StatusEnum.USER_REGISTERED);
        }
    }

    // ????????? ??????????????? ???????????? ??????
    @Override
    public void sendMail(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress()); // ???????????? ??????
        message.setFrom("jenyglee30@gmail.com"); // ????????? ?????? ??????
        message.setSubject(mailDto.getTitle()); // ?????? ??????
        message.setText(mailDto.getMessage()); // ?????? ??????

        javaMailSender.send(message);
    }

    // ???????????? ????????? ???????????? ??????
    @Override
    public boolean userEmailCheck(String userEmail, String userName) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        if (user != null && user.getNickName().equals(userName)) {
            return true;
        } else {
            return false;
        }
    }

    // ???????????? ????????? ?????? ??? DTO??? ??????
    @Override
    public MailDto createMailAndChangePassword(String userEmail, String userName) {
        String str = getTempPassword();
        MailDto dto = new MailDto();
        dto.setAddress(userEmail);
        dto.setTitle(userName + "?????? ?????? ?????????????????? ?????? ????????? ?????????.");
        dto.setMessage(
            "???????????????. ?????? ?????????????????? ?????? ?????? ????????? ?????????." + "[" + userName + "]" + "?????? ?????? ??????????????? "
                + str + " ?????????.");
        updatePassword(str, userEmail);
        return dto;
    }

    // ?????? ??????????????? ??????
    public void updatePassword(String str, String userEmail) {
        String pw = encoder.encode(str);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        user.updateUserPassword(pw);
        userRepository.save(user);
    }

    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }


}