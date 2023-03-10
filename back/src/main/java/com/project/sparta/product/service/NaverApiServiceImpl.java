package com.project.sparta.product.service;

import com.project.sparta.common.dto.PageResponseDto;
import com.project.sparta.product.dto.ProductResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NaverApiServiceImpl implements NaverApiService {

    @Override
    public PageResponseDto<List<ProductResponseDto>> searchItems(String query , int limit, int offset, String sort) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "BRDOYb9YmnTIgDCwI0MT");
        headers.add("X-Naver-Client-Secret", "_DVfDr_c2g");
        String body = "";

        // 현재 페이지(offset)에 따라 몇번째 상품부터 보여줄지(start)
        int start = 1;
        if(offset > 1){
            start = (offset - 1) * limit + 1;
        }

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest
                .exchange("https://openapi.naver.com/v1/search/shop.json?" +
                        "display=" + limit + // 몇개씩 노출
                        "&query=" + query + // 상품 검색어
                        "&start=" + start + // 몇번째 상품부터 노출
                        "&sort=" + sort, // 정렬 기준
                        HttpMethod.GET,
                        requestEntity,
                        String.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        log.info("NAVER API Status Code : " + status);

        String response = responseEntity.getBody();
        return fromJSONtoItems(response, offset);
    }


    public PageResponseDto<List<ProductResponseDto>> fromJSONtoItems(String response, int offset) {

        JSONObject rjson = new JSONObject(response);
        
        JSONArray items  = rjson.getJSONArray("items");
        int total = rjson.getInt("total");
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();

        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            ProductResponseDto productResponseDto = new ProductResponseDto(itemJson);
            productResponseDtoList.add(productResponseDto);
        }

        return new PageResponseDto<>(offset, total, productResponseDtoList);
    }
}