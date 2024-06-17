package jp.co.metateam.library.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

//import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jp.co.metateam.library.values.RentalStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 貸出管理DTO
 */
@Getter //この@Getterと@Setterなに？調べてもでてこない
@Setter
public class RentalManageDto {

    private Long id;

    @NotEmpty(message="在庫管理番号は必須です")
    private String stockId;
    //privateでstockIdという名前のメンバ変数を定義している。

    @NotEmpty(message="社員番号は必須です")
    private String employeeId;

    @NotNull(message="貸出ステータスは必須です")
    private Integer status;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="貸出予定日は必須です")
    private Date expectedRentalOn;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message="返却予定日は必須です")
    
    private Date expectedReturnOn;

    private Timestamp rentaledAt;

    private Timestamp returnedAt;

    private Timestamp canceledAt;

   // private Stock stock;
    public Stock stock;

    private Account account;
      //private Long id;とかはメンバ変数を定義しているところ。
    //privateなので他のクラスから値を代入したり取得したりできないが、setName()メソッドを加えることで
    //他のクラスから関節的にメンバ変数へ値を設定することができる。

public void setBookMst(BookMst bookMst) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBookMst'");
    }

public String ValidationComfirm(Integer preStatus){
    Date currentDate = new Date();//なんでこれインスタンス化したの？
   if(preStatus == RentalStatus.RENT_WAIT.getValue() && this.status == RentalStatus.RETURNED.getValue()){
      return "「 貸出待ち」から「返却済み」へ選択することはできません。";
      //preStatusが貸出待ちかつRentalStatusが返却済みのとき、エラーを返す。
   } else if(preStatus == RentalStatus.RENTAlING.getValue() && this.status == RentalStatus.RENT_WAIT.getValue()){
      return "「貸出中」から「貸出待ち」へ選択することはできません。"; 
   } else if(preStatus == RentalStatus.RENTAlING.getValue() && this.status == RentalStatus.CANCELED.getValue()){
      return "「貸出中」から「キャンセル」へ選択することはできません。"; 
   } else if(preStatus == RentalStatus.RETURNED.getValue() && this.status == RentalStatus.RENT_WAIT.getValue()){
        return "「返却済み」から「貸出待ち」へ選択することはできません。"; 
   } else if(preStatus == RentalStatus.RETURNED.getValue() && this.status == RentalStatus.RENTAlING.getValue()){
        return "「返却済み」から「貸出中」へ選択することはできません。"; 
    } else if(preStatus == RentalStatus.RETURNED.getValue() && this.status == RentalStatus.CANCELED.getValue()){
        return "「返却済み」から「キャンセル」へ選択することはできません。"; 
    } else if(preStatus == RentalStatus.CANCELED.getValue() && this.status == RentalStatus.RENT_WAIT.getValue()){
        return "「キャンセル」から「貸出待ち」へ選択することはできません。"; 
    } else if(preStatus == RentalStatus.CANCELED.getValue() && this.status == RentalStatus.RENTAlING.getValue()){
        return "「キャンセル」から「貸出中」へ選択することはできません。"; 
    } else if(preStatus == RentalStatus.CANCELED.getValue() && this.status == RentalStatus.RETURNED.getValue()){
        return "「キャンセル」から「返却済み」へ選択することはできません。"; 
     }else if(status == RentalStatus.RENTAlING.getValue() && expectedRentalOn.after(currentDate)){
        return "貸出ステータスが貸出中の時、未来の日付を貸出予定日として選択することはできません";
    }else if(status == RentalStatus.RETURNED.getValue() && expectedReturnOn.after(currentDate)){
        return "貸出ステータスが返却済みの時、未来の日付を返却予定日として選択することはできません";
    }else if(status == RentalStatus.RENTAlING.getValue() && expectedRentalOn.before(currentDate)){
        return "貸出予定日は現在日時よりも後の日付を入力してください。";

    }

    return null;
  
     }



}
