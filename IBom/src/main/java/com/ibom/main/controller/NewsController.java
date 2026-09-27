package com.ibom.main.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.PolicyRepository;
import com.ibom.main.dao.PolicyBookmarkRepository;
import com.ibom.main.model.Policy;
import com.ibom.main.model.PolicyBookmark;

import jakarta.servlet.http.HttpSession;

@Controller
public class NewsController {

    private final PolicyRepository policyRepository;
    private final PolicyBookmarkRepository bookmarkRepository;

    public NewsController(PolicyRepository policyRepository,
                          PolicyBookmarkRepository bookmarkRepository) {
        this.policyRepository = policyRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    /** 육아소식 목록 (검색 · 필터) */
    @GetMapping("/news")
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String scope,
                       @RequestParam(required = false) String region,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false, defaultValue = "latest") String sort,
                       Model model) {

        String categoryValue = normalize(category);
        String scopeValue    = normalize(scope);
        String regionValue   = normalize(region);
        String keywordValue  = (keyword == null) ? "" : keyword.trim();

        // 등록일이 같은 글이 섞이지 않도록 ID를 두 번째 기준으로 둠
        Sort sortOption = "oldest".equals(sort)
                ? Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"))
                : Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

        List<Policy> policies =
                policyRepository.search(
                        categoryValue,
                        scopeValue,
                        regionValue,
                        keywordValue,
                        sortOption);

        model.addAttribute("policies", policies);
        model.addAttribute("totalCount", policies.size());

        // 화면이 선택 상태를 그대로 유지하도록 조건을 돌려줌
        model.addAttribute("category",
                categoryValue.isEmpty() ? "ALL" : categoryValue);

        model.addAttribute("scope",
                scopeValue.isEmpty() ? "ALL" : scopeValue);

        model.addAttribute("region",
                regionValue.isEmpty() ? "ALL" : regionValue);

        model.addAttribute("keyword", keywordValue);
        model.addAttribute("sort", sort);

        return "news/news";
    }

    /** 육아소식 상세 */
    @GetMapping("/news/detail")
    public String detail(@RequestParam Long id,
                         HttpSession session,
                         Model model) {

        Policy policy = policyRepository.findById(id).orElse(null);

        // 없는 글이거나 숨김 처리된 글이면 목록으로 되돌림
        if (policy == null || Boolean.FALSE.equals(policy.getIsActive())) {
            return "redirect:/news";
        }

        model.addAttribute("policy", policy);
        model.addAttribute("scopeLabel", scopeLabel(policy.getScope()));

        // 현재 사용자가 이 게시글을 북마크했는지 확인
        model.addAttribute("bookmarked",
                bookmarkRepository.existsByUserIdAndPolicyId(
                        currentUserId(session), id));

        // 이전 글
        model.addAttribute("prev",
                policyRepository
                        .findFirstByIsActiveTrueAndIdLessThanOrderByIdDesc(id)
                        .orElse(null));

        // 다음 글
        model.addAttribute("next",
                policyRepository
                        .findFirstByIsActiveTrueAndIdGreaterThanOrderByIdAsc(id)
                        .orElse(null));

        return "news/newsDetail";
    }

    /** DB에 저장된 영문 값을 화면에 보여줄 한글로 */
    private String scopeLabel(String scope) {
        if ("GOVERNMENT".equals(scope)) return "중앙정부";
        if ("METRO".equals(scope))      return "광역자치단체";
        if ("LOCAL".equals(scope))      return "기초자치단체";
        return scope;
    }

    /** 북마크 켜고 끄기 */
    @PostMapping("/news/bookmark")
    public String toggleBookmark(@RequestParam Long id,
                                 HttpSession session) {

        Long userId = currentUserId(session);

        // 이미 북마크되어 있다면 → 삭제
        if (bookmarkRepository.existsByUserIdAndPolicyId(userId, id)) {
            bookmarkRepository.deleteByUserIdAndPolicyId(userId, id);
        }
        // 북마크되어 있지 않다면 → 저장
        else {
            PolicyBookmark bookmark = new PolicyBookmark();
            bookmark.setUserId(userId);
            bookmark.setPolicyId(id);

            bookmarkRepository.save(bookmark);
        }

        // 처리 후 상세 페이지로 다시 이동
        return "redirect:/news/detail?id=" + id;
    }

    /**
     * 지금 로그인한 회원 번호.
     *
     * TODO: 실제 로그인 기능이 연결되면
     * 아래 임시값 1L을 실제 로그인 사용자 ID로 변경해야 함.
     */
    private Long currentUserId(HttpSession session) {

        Object loginUserId = session.getAttribute("loginUserId");

        return (loginUserId != null)
                ? (Long) loginUserId
                : 1L;
    }

    /** "ALL" 이거나 비어 있으면 조건을 걸지 않겠다는 뜻으로 빈 문자열로 바꿈 */
    private String normalize(String value) {
        if (value == null || value.isBlank() || "ALL".equals(value)) {
            return "";
        }

        return value;
    }
}