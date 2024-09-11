package jp.co.metateam.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.ReviewMstDto;
import jp.co.metateam.library.service.ReviewMstService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller

public class ReviewController {

    @Autowired
    private ReviewMstService reviewMstService;

    @GetMapping("/review/index")
    public String index(Model model) {

        List<ReviewMstDto> reviewMstList = this.reviewMstService.reviewValues();

        model.addAttribute("reviewMstList", reviewMstList);

        return "review/index";

    }

    @GetMapping("/review/{bookId}/detail")
    public String detail(@PathVariable("bookId") Long bookId, Model model) {
        // 書籍タイトルで書籍情報を取得
        List<ReviewMstDto> reviewMstList = reviewMstService.findByBookId(bookId);
        
        // 空のReviewMstDtoを作成してモデルに追加
        model.addAttribute("reviewMstDto", new ReviewMstDto());
        model.addAttribute("reviewMstList", reviewMstList);
        
        return "review/detail";
    }

    @PostMapping("/review/{bookId}/detail")
    public String save(@PathVariable("bookId") Long bookId, 
                       @Valid @ModelAttribute ReviewMstDto reviewMstDto, 
                       BindingResult result, 
                       Model model, 
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            // バリデーションエラーがある場合はフォームを再表示
            model.addAttribute("reviewMstList", reviewMstService.findByBookId(bookId));
            return "review/detail";
        }

        try {
            this.reviewMstService.save(reviewMstDto);
            return "redirect:/review/index";
        } catch (Exception e) {
            log.error(e.getMessage());
            // エラーが発生した場合、エラーメッセージを再表示
            model.addAttribute("reviewMstList", reviewMstService.findByBookId(bookId));
            model.addAttribute("reviewMstDto", reviewMstDto);
            model.addAttribute("errorMessage", "An error occurred while saving the review.");
            return "review/detail";
        }
    }
     }
    

