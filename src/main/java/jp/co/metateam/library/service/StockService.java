package jp.co.metateam.library.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.constants.Constants;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.CalendarDto;
import jp.co.metateam.library.model.DateCalendarDto;
import jp.co.metateam.library.model.ReviewMstDto;
import jp.co.metateam.library.model.Stock;
import jp.co.metateam.library.model.StockDto;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.repository.StockRepository;

@Service // MVCのどれに該当するかを最初に記述し、SpringのDIコンテナにbeanとして登録
public class StockService {
    private static final String Title = null;
    private final BookMstRepository bookMstRepository;
    private final StockRepository stockRepository;

    @Autowired
    public StockService(BookMstRepository bookMstRepository, StockRepository stockRepository) {
        this.bookMstRepository = bookMstRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public List<Stock> findAll() {
        List<Stock> stocks = this.stockRepository.findByDeletedAtIsNull();

        return stocks;
    }

    @Transactional
    public List<Stock> findStockAvailableAll() {
        List<Stock> stocks = this.stockRepository.findByDeletedAtIsNullAndStatus(Constants.STOCK_AVAILABLE);

        return stocks;
    }

    @Transactional
    public Stock findById(String id) {
        return this.stockRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(StockDto stockDto) throws Exception {
        try {
            Stock stock = new Stock();
            BookMst bookMst = this.bookMstRepository.findById(stockDto.getBookId()).orElse(null);
            if (bookMst == null) {
                throw new Exception("BookMst record not found.");
            }

            stock.setBookMst(bookMst);
            stock.setId(stockDto.getId());
            stock.setStatus(stockDto.getStatus());
            stock.setPrice(stockDto.getPrice());

            // データベースへの保存
            this.stockRepository.save(stock);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public void update(String id, StockDto stockDto) throws Exception {
        try {
            Stock stock = findById(id);
            if (stock == null) {
                throw new Exception("Stock record not found.");
            }

            BookMst bookMst = stock.getBookMst();
            if (bookMst == null) {
                throw new Exception("BookMst record not found.");
            }

            stock.setId(stockDto.getId());
            stock.setBookMst(bookMst);
            stock.setStatus(stockDto.getStatus());
            stock.setPrice(stockDto.getPrice());

            // データベースへの保存
            this.stockRepository.save(stock);
        } catch (Exception e) {
            throw e;
        }
    }

    // ↴stockControllerの@GetMapping("stock/calendar")のList<dayOfWeek>～でこのメソッドに飛ぶ。【DaysOfWeek日付のほう！】
    // このメソッドのint yearとかがコントローラーから来た引数の受け入れるデータ型と変数名。
    public List<Object> generateDaysOfWeek(int year, int month, LocalDate startDate, int daysInMonth) {
        List<Object> daysOfWeek = new ArrayList<>();
        for (int dayOfMonth = 1; dayOfMonth <= daysInMonth; dayOfMonth++) {
            LocalDate date = LocalDate.of(year, month, dayOfMonth);
            DateTimeFormatter formmater = DateTimeFormatter.ofPattern("dd(E)", Locale.JAPANESE);
            daysOfWeek.add(date.format(formmater));
        }

        return daysOfWeek;
    }
    // 【Values 在庫数、書籍名、貸出可能数のほう！】

    public List<CalendarDto> generateValues(Integer year, Integer month, Integer daysInMonth) {

        // FIXME ここで各書籍毎の日々の在庫を生成する処理を実装する
        // FIXME ランダムに値を返却するサンプルを実装している

        List<Object[]> countByAvailableBooks = this.stockRepository.countByLendableBook();
        List<CalendarDto> wholeValue = new ArrayList<>();
        // インスタンス化。最終的に戻り値としてwholeValueにこのメソッドで使う全てのListが格納される。

        for (int i = 0; i < countByAvailableBooks.size(); i++) {

            List<DateCalendarDto> calendarDtoListPerDay = new ArrayList<>();
            CalendarDto calendarDto = new CalendarDto();

            Object[] bookInfo = countByAvailableBooks.get(i);
            String title = (String) bookInfo[0];
            // 0番目の要素に書籍タイトルがある。
            Long count = (Long) bookInfo[1];
            // 1番目の要素に書籍タイトルに対する在庫数がある。
            calendarDto.setTitle(title);
            calendarDto.setStockCount(count);

            // ここまでで書籍名、利用可能在庫数を出すことができた。

            for (int day = 1; day <= daysInMonth; day++) {

                DateCalendarDto dateCalendarDto = new DateCalendarDto();
                LocalDate currentDate = LocalDate.of(year, month, day);
                Date specifiedDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                dateCalendarDto.setExpectedRentalOn(specifiedDate);

                List<String> stockIdList = this.stockRepository.stockId(title, specifiedDate);
                // ここもレポジトリから得たデータをstockIdListへ格納している。

                if (stockIdList != null && stockIdList.size() >= 1) {
                    dateCalendarDto.setStockId(stockIdList.get(0));
                }

                Long rentaledBook = this.stockRepository.countByrentaledBook(title, specifiedDate);
                Long availableBookNum = count - rentaledBook;
                dateCalendarDto.setAvailableBookNum(availableBookNum);

                calendarDtoListPerDay.add(dateCalendarDto);
            }
            calendarDto.setCalendarDtoListPerDay(calendarDtoListPerDay);
            wholeValue.add(calendarDto);

        }

        return wholeValue;

    }

    // public List<ReviewMstDto> reviewValues() {
    //     List<ReviewMst> reviews = this.stockRepository.findAll();
    //     List<ReviewMstDto> reviewMstDtoList = new ArrayList<ReviewMstDto>();

    //     // 書籍名をリストに追加する
    //     // bookNames.add("花丸伝説");
    //     // bookNames.add("卵ポーロ");
    //     // bookNames.add("1984");
    //     // bookNames.add("Pride and Prejudice");
    //     // bookNames.add("どこへ行った西園寺");

    //     return bookNames;

    // }

    public List<String> reviewDetailValues() {

        List<String> bookreviews = new ArrayList<>();

        // 書籍名をリストに追加する
        bookreviews.add("おもろい");
        bookreviews.add("サイコー");
        bookreviews.add("1984");
        bookreviews.add("insaine");
        bookreviews.add("さいあく");

        return bookreviews;

    }

}
