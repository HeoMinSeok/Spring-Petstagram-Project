package com.petstagram.dto;

import lombok.Getter;

@Getter
public class ImageDTO {
    private Long id;
    private String imageUrl;
    private Long postId;
}
