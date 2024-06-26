package jp.co.metateam.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.metateam.library.model.RentalManage;

@Repository//MVCのどれに該当するかを記述
public interface RentalManageRepository extends JpaRepository<RentalManage, Long> {
    List<RentalManage> findAll();

	Optional<RentalManage> findById(Long id);
//ここから追加
    @Query 
    //貸出登録の可否チェック用のデータを取得してListへデータの格納をしている。　?! =渡した値が入る。
    (" SELECT rm FROM RentalManage rm " 
    + " WHERE ( rm.status=0 OR rm.status=1 ) " 
    +" AND ?1 = rm.stock.id ")
    List<RentalManage> findByStockIdAndStatusIn(String StockId);
    //RentalManageエンティティからデータを取得している。statusが0か1であり、かつ指定されたStockIdデータを取得(?


    @Query
    //貸出編集の可否チェック用のデータを取得している。
    (" SELECT rm FROM RentalManage rm " 
    + " WHERE ( rm.status=0 OR rm.status=1 ) " 
    + " AND ?1 = rm.stock.id "
    + " AND ?2 <> rm.id")
    List<RentalManage> findByStockIdAndStatusIn(String StockId, Long rentalId);

    




}
