package jp.co.metateam.library.controller;
import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.RentalManage;
import jp.co.metateam.library.model.RentalManageDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;
import jp.co.metateam.library.service.AccountService;
import jp.co.metateam.library.service.RentalManageService;
import jp.co.metateam.library.service.StockService;
import jp.co.metateam.library.values.RentalStatus;
import jp.co.metateam.library.constants.Constants;
import lombok.extern.log4j.Log4j2;
//ステータス表示のため

/**
 * 貸出管理関連クラスß
 */
@Log4j2
@Controller
public class RentalManageController {

    private final AccountService accountService;
    private final RentalManageService rentalManageService;
    private final StockService stockService;

    @Autowired
    public RentalManageController(
        AccountService accountService, 
        RentalManageService rentalManageService, 
        StockService stockService
    ) {
        this.accountService = accountService;
        this.rentalManageService = rentalManageService;
        this.stockService = stockService;
    }

    /**
     * 貸出一覧画面初期表示
     * @param model
     * @return
     */
    @GetMapping("/rental/index")
    public String index(Model model) {
        // 貸出管理テーブルから全件取得
        List<RentalManage> rentalManageList = this.rentalManageService.findAll();
        // 貸出一覧画面に渡すデータをmodelに追加
        model.addAttribute("rentalManageList", rentalManageList);
        // 貸出一覧画面に遷移
        return "rental/index";
    }

    /**
     * @param model
     * @return
     */
    @GetMapping("/rental/add") 

    public String add(Model model) { //String は戻り値の型。戻り値がなければvoidと記述。(ちなみにmainメソッドには戻り値がないので必ずvoidとなる。)
     //ModelクラスはSpringの機能で、ControllerからView(画面)へ変数を渡すためのもの！
      List<Account> accountList = this.accountService.findAll(); 
      //ここでは、アカウントオブジェクトのレコードを取得してaccountListというリストに格納している！
      List<Stock> stockList = this.stockService.findAll(); 
   // String attributeName; 

    model.addAttribute("rentalStatus", RentalStatus.values()); 
    model.addAttribute("accounts", accountList); 
    model.addAttribute("stockList", stockList); 

    if (!model.containsAttribute("rentalManageDto")) { 
          model.addAttribute("rentalManageDto", new RentalManageDto()); 
        } 

      return "rental/add"; 

  } 


  @PostMapping("/rental/add")
  public String save(@Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result,RedirectAttributes ra) {
   
     try{

      if(result.hasErrors()){
       //      String message;
            throw new Exception("Validation error.");

      }
      
      this.rentalManageService.save(rentalManageDto);

      return "redirect:/rental/index";
    } catch (Exception e){
      log.error(e.getMessage());

      ra.addFlashAttribute("rentalManageDto", rentalManageDto);
      ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManageDto", result);

      return "redirect:/rental/add";

    }

  }


  @GetMapping("/rental/{id}/edit")
  public String edit(@PathVariable("id") String id, Model model) {
    //@PathVariableでidというパス変数を受け取り、その値を使ってユーザー情報を取得している。　
    //Model modelは画面に値を引き渡す働きがある。
      List<Account> accounts = this.accountService.findAll();
      List <Stock> stockList = this.stockService.findStockAvailableAll();
      //このfindAllメソッドはデータベース内のすべてのレコードを一括で取得する際に便利なメソッド！List<>の<>の中身は円ティティクラスを示す。戻り値として該当するエンティティのリストが返される。


      model.addAttribute("accounts", accounts);
      model.addAttribute("stockList", stockList);
      model.addAttribute("rentalStatus", RentalStatus.values());
      //右のやつを左に入れている！！model.addAttribute("sample", str) は、Modelにキーと値をセットします。
      //model.　は表示する機能。moidel.addAttributeメソッドで画面に渡したいデータをModelオブジェクトに追加
      // model.addAttribute("属性名",渡したいデータ)

      if (!model.containsAttribute("rentalManage")) {
          RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
          RentalManageDto rentalManageDto = new RentalManageDto();

          rentalManageDto.setId(rentalManage.getId());
          //取得したIdをrentalManageDtoにセットしている！
          rentalManageDto.setStockId(rentalManage.getStock().getId());
          //取得した在庫管理番号をrenntalManageDtoにセットしている！
          rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
          rentalManageDto.setStatus(rentalManage.getStatus());
          rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
          rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());

          model.addAttribute("rentalManage", rentalManageDto);
          //セットしたデータを表示させる。
      }

      return "rental/edit";
  }

  @PostMapping("/rental/{id}/edit")
  public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra)throws Exception {
      try {
 feature/rental/rental-check
   
          //エンティティのIDに基づいて変更前情報を取得

          //変更前情報を取得
 develop
          RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
          //変更後のステータスを渡してDtoでバリデーションチェック
          String ValidationError = rentalManageDto.ValidationComfirm(rentalManage.getStatus());
         
          if(ValidationError != null){
              result.addError(new FieldError("rentalManage", "status", ValidationError));
          }
 feature/rental/rental-check
   
          

          //バリデーションエラーがあるかを判別。エラーあり：例外を投げる エラーなし：登録処理に移る
 develop
          if (result.hasErrors()) {
              throw new Exception("Validation error.");
          }
          //バリデーションエラーがあるかを判別。エラーあり：例外を投げる エラーなし：登録処理に移る
          
          Long rentalManageId = Long.valueOf(id);
          rentalManageService.update(rentalManageId, rentalManageDto);
        // 登録処理　ifでエラー筆禍らなければこちらの処理を行う。  
        //rentalManageId,rentalManageDtoのデータをrentalManageServiceにぶち込む
   
          return "redirect:/rental/index";
      //エラーが発生すると入力したデータはDBに登録されずに編集画面に返す
         } catch (Exception e) {
           log.error(e.getMessage());
           
           ra.addFlashAttribute("rentalManage", rentalManageDto);
           ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManage", result);
          //ra.addFlash～ってなに？raはなんなん
           return "redirect:/rental/" + id +"/edit";
       }
  }
//ここから追加
  @Query
  //@Query:データベースからデータを取り出したり修正したりする命令を出せるアノテーション
  //貸出登録の貸出可否チェックのメソッド
  public String findAvailableWithRentalDate(RentalManageDto rentalManageDto, String stockId){
         List<RentalManage> rentalAvailable = this.rentalManageService.findByStockIdAndStatusIn(stockId);

         for(RentalManage exist:rentalAvailable){

         if(exist.getExpectedRentalOn().compareTo(rentalManageDto.getExpectedReturnOn())<=0 && rentalManageDto.getExpectedRentalOn().compareTo(exist.getExpectedReturnOn())<=0){
  //リスト内の貸し出し予定日<=Dtoの返却予定日　かつ　Dtoの貸出予定日<=リスト内の返却予定日 　→エラー処理
             return "選択された日付は既に登録された貸出情報と重複しています。";

             }
 
         }

        return null;
  }
//貸出編集の貸出可否チェックのメソッド
public String findAvailableWithRentalDate(RentalManageDto rentalManageDto, Long rentalId){
    List<RentalManage> rentalAvailable = this.rentalManageService.findByStockIdAndStatusIn(rentalManageDto.getStockId(),rentalId);

    for(RentalManage exist:rentalAvailable){

    if(exist.getExpectedRentalOn().compareTo(rentalManageDto.getExpectedReturnOn())<=0 && rentalManageDto.getExpectedRentalOn().compareTo(exist.getExpectedReturnOn())<=0){

        return "選択された日付は既に登録された貸出情報と重複しています。";

              }

         }

        return null;
}



}
