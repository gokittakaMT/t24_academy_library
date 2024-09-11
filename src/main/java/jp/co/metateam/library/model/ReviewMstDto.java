package jp.co.metateam.library.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReviewMstDto {

    private String bookTitle;
    private Long reviewCount;
    private Long id;
    private String title;
    private Long bookId;
     @NotBlank(message = "口コミを記入してください。")
     @Size(max = 300, message = "300文字以内で入力してください。")
    private String review;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
        // 詳細画面につなげるため
    }

    public String getReview() {

        return review;

    }

    public void setReview(String review) {

        this.review = review;
    }

}
