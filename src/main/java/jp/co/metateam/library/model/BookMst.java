package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * 書籍マスタ
 */
@Entity
@Table(name = "BookMst")
public class BookMst {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //  @Column(name = "book_id")
    //  private Long bookId;//review機能で追加
    /** ISBN */
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    /** 書籍タイトル */
    @Column(name = "title", nullable = false)
    private String title;

    /** 削除日時 */
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @OneToMany(mappedBy = "bookMst", cascade = CascadeType.ALL)
    public List<Stock> stock;

    /** Getters */

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    /** Setters */

    public void setId(Long id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
