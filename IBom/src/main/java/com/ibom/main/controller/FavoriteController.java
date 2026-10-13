package com.ibom.main.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.PostFavoriteRepository;
import com.ibom.main.model.PostFavorite;

import jakarta.servlet.http.HttpSession;

@Controller
public class FavoriteController {

    private final PostFavoriteRepository postFavoriteRepository;

    public FavoriteController(PostFavoriteRepository postFavoriteRepository) {
        this.postFavoriteRepository = postFavoriteRepository;
    }

    /** 관심 누르기 / 취소 (한 번 더 누르면 취소) */
    @PostMapping("/favorite/toggle")
    public String toggle(@RequestParam("postId") Long postId,
                         @RequestParam(value = "back", required = false) String back,
                         HttpSession session) {

        Long userId = (Long) session.getAttribute("loginUserId");
        if (userId == null) {
            userId = (Long) session.getAttribute("userID");
        }
        if (userId == null) {
            return "redirect:/member/login";
        }

        if (postFavoriteRepository.existsByUserIdAndPostId(userId, postId)) {

            postFavoriteRepository.deleteByUserIdAndPostId(userId, postId);

        } else {

            PostFavorite favorite = new PostFavorite();
            favorite.setUserId(userId);
            favorite.setPostId(postId);
            favorite.setCreatedAt(LocalDateTime.now());

            postFavoriteRepository.save(favorite);
        }

        /* 누른 자리로 돌아간다. '/' 로 시작하는 주소만 허용 (외부 사이트로 튕기는 것 방지) */
        if (back != null && back.startsWith("/") && !back.startsWith("//")) {
            return "redirect:" + back;
        }

        return "redirect:/board/share";
    }
}