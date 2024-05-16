package jp.co.metateam.library.controller;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
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

    public String add(Model model) { 
      List<Account> accountList = this.accountService.findAll(); 

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
  public String save(@valid @ModelAttribute RentalManageDto rentalManageDto, BindinResult result,RedirectAttributes ra) {
   
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
      List<Account> accounts = this.accountService.findAll();
      List <Stock> stockList = this.stockService.findStockAvailableAll();

      model.addAttribute("accounts", accounts);
      model.addAttribute("stockList", stockList);
      model.addAttribute("rentalStatus", RentalStatus.values());

      if (!model.containsAttribute("rentalManage")) {
          RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
          RentalManageDto rentalManageDto = new RentalManageDto();

          rentalManageDto.setId(rentalManage.getId());
          rentalManageDto.setStockId(rentalManage.getStock().getId());
          rentalManageDto.setEmployeeId(rentalManage.getAccount().getEmployeeId());
          rentalManageDto.setStatus(rentalManage.getStatus());
          rentalManageDto.setExpectedRentalOn(rentalManage.getExpectedRentalOn());
          rentalManageDto.setExpectedReturnOn(rentalManage.getExpectedReturnOn());

          model.addAttribute("rentalManage", rentalManageDto);
      }

      return "rental/edit";
  }

  @PostMapping("/rental/{id}/edit")
  public String update(@PathVariable("id") String id, @Valid @ModelAttribute RentalManageDto rentalManageDto, BindingResult result, RedirectAttributes ra)throws Exception {
      try {
   
          //変更前情報を取得
          RentalManage rentalManage = this.rentalManageService.findById(Long.valueOf(id));
          //変更後のステータスを渡してDtoでバリデーションチェック
          String ValidationError = rentalManageDto.ValidationComfirm(rentalManage.getStatus());
         
          if(ValidationError != null){
              result.addError(new FieldError("rentalManage", "status", ValidationError));
          }
   
          //バリデーションエラーがあるかを判別。エラーあり：例外を投げる エラーなし：登録処理に移る
          if (result.hasErrors()) {
              throw new Exception("Validation error.");
          }
          // 登録処理
          Long rentalManageId = Long.valueOf(id);
          rentalManageService.update(rentalManageId, rentalManageDto);
   
          return "redirect:/rental/index";
      //エラーが発生すると入力したデータはDBに登録されずに編集画面に返す
       } catch (Exception e) {
           log.error(e.getMessage());
           
           ra.addFlashAttribute("rentalManage", rentalManageDto);
           ra.addFlashAttribute("org.springframework.validation.BindingResult.rentalManage", result);
   
           return "redirect:/rental/" + id +"/edit";
       }
  }

}
