package jp.co.metateam.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jp.co.metateam.library.model.ReviewMst;
import java.util.List;

public interface ReviewMstRepository extends JpaRepository<ReviewMst, Long> {

    @Query(value = "SELECT b.id AS book_id,b.title AS book_title, COUNT(r.id) AS review_count FROM book_mst b LEFT JOIN review_mst r ON b.id = r.book_id GROUP BY b.id, b.title ORDER BY b.id;", nativeQuery = true)
    List<Object[]> findAllReviewsWithBookTitles();

    @Query(value = "SELECT b.id AS book_id, b.title AS book_title, r.review FROM book_mst b LEFT JOIN review_mst r ON b.id = r.book_id WHERE b.id = :bookId ORDER BY r.id;", nativeQuery = true)
    //:bookIdで該当する書籍(引数として与えられたbookIdの値)の情報のみ取得。
    List<Object[]> findAllReview(Long bookId);
      
    @Modifying
    @Query(value ="INSERT INTO review_mst (review, book_id) values (?1, ?2)", nativeQuery = true)
    void insertReview(String review , long bookId);

}
