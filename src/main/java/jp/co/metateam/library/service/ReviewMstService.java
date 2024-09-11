package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.ReviewMstDto;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.repository.ReviewMstRepository;

@Service
public class ReviewMstService {

    @Autowired
    private final ReviewMstRepository reviewMstRepository;

    @Autowired
    private BookMstRepository bookMstRepository;

    public ReviewMstService(ReviewMstRepository reviewMstRepository) {

        this.reviewMstRepository = reviewMstRepository;

    }

    public List<ReviewMstDto> reviewValues() {
        List<Object[]> results = this.reviewMstRepository.findAllReviewsWithBookTitles();
        List<ReviewMstDto> reviewMstDtoList = new ArrayList<>();// 最後に必要な情報をこのリストにすべて格納する。

        for (Object[] result : results) {

            Long bookId = (Long) result[0];
            String bookTitle = (String) result[1];
            // String review = (String) result[2];
            Long reviewCount = ((Number) result[2]).longValue();

            ReviewMstDto reviewMstDto = new ReviewMstDto();
            reviewMstDto.setBookId(bookId);
            reviewMstDto.setBookTitle(bookTitle);
            // reviewMstDto.setReview(review);
            reviewMstDto.setReviewCount(reviewCount);

            reviewMstDtoList.add(reviewMstDto);

        }

        return reviewMstDtoList;

    }

    public List<ReviewMstDto> findByBookId(Long bookId) {
        List<ReviewMstDto> reviews = new ArrayList<>();
        List<Object[]> results = reviewMstRepository.findAllReview(bookId);
    
        for (Object[] result : results) {
            Long resultBookId = ((Number) result[0]).longValue();
            ReviewMstDto subValue = new ReviewMstDto();
            subValue.setBookId(resultBookId);
            subValue.setTitle((String) result[1]);
            subValue.setReview((String) result[2]);
            
            reviews.add(subValue);
        }
        return reviews;
    }

    @Transactional
    public void save(ReviewMstDto reviewMstDto) throws Exception {
        //reviewMstDtoにサイトで入力されたレビューが入っている。
        try {
            BookMst bookMst = bookMstRepository.findById(reviewMstDto.getBookId()).orElse(null);
        //このbookMstの中には該当するbookIdが入っている。
            if (bookMst == null) {
                throw new Exception("BookMst not found.");
            }
                       
            this.reviewMstRepository.insertReview(reviewMstDto.getReview(),bookMst.getId());

        } catch (Exception e) {
            throw e;
        }
    }

    public boolean isValidReview(String review, BindingResult result) {
        boolean hasErrors = false;
        
        if (StringUtils.isEmpty(review)) {
            result.rejectValue("review", "error.review", "口コミは必須です。");
            hasErrors = true;
        } else if (review.length() != 300) {
            result.rejectValue("review", "error.review", "口コミは300文字で入力してください。");
            hasErrors = true;
        }
        
        return hasErrors;
    }
}
