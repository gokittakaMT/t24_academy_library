package jp.co.metateam.library.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findAll();

    List<Stock> findByDeletedAtIsNull();

    List<Stock> findByDeletedAtIsNullAndStatus(Integer status);

    Optional<Stock> findById(String id);

    List<Stock> findByBookMstIdAndStatus(Long book_id, Integer status);

    // 在庫カレンダー用の追加↴

    // @Query(value = " select m.title from BookMst m ") // select title from
    // book_mst;
    // List<String> findByTitle(); // 書籍名取得

    // @Query(value = "SELECT COUNT(*) AS book_count from Stock m GROUP BY
    // m.bookMst.id")
    // List<String> findByBookNumber();//書籍名ごとの在庫数

    // @Query(value = "SELECT COUNT(*) AS book_count,title FROM stocks JOIN book_mst ON stocks.book_id = book_mst.id GROUP BY book_id", nativeQuery = true)
    // List<Object[]> titleAndBookNumbers(); // それぞれの書籍名に対する在庫数と書籍名の取得

    @Query("SELECT s.bookMst.title, COUNT(s) FROM Stock s JOIN s.bookMst WHERE s.status = 0 GROUP BY s.bookMst.title")
    List<Object[]> countByLendableBook();

    @Query(value = "SELECT s.id FROM stocks s LEFT JOIN rental_manage rm ON s.id = rm.stock_id JOIN book_mst bm ON s.book_id = bm.id WHERE s.status = 0 AND bm.title = ?1 AND (rm.stock_id IS NULL OR rm.expected_rental_on > ?2 OR rm.expected_return_on < ?2 OR rm.status = 3);", nativeQuery = true)
    List<String> stockId(String title, java.util.Date specifiedDate);

    @Query(value = "SELECT COUNT(*) FROM rental_manage rm INNER JOIN stocks s ON rm.stock_id = s.id INNER JOIN book_mst bm ON s.book_id = bm.id WHERE bm.title=?1  AND (rm.expected_rental_on <= ?2 AND rm.expected_return_on >= ?2)", nativeQuery = true)
    Long countByrentaledBook(String title, java.util.Date specifiedDate);
}
