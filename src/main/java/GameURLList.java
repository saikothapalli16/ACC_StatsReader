import java.util.ArrayList;
import java.util.List;

public class GameURLList {
  private Date startDate;
  private Date endDate;
  private List<String> urlList;
  private DailyGameMethods dailyGameMethods;

  public GameURLList(Date startDate, Date endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.urlList = new ArrayList<>();
    this.dailyGameMethods = new DailyGameMethods();
  }

  private List<String> urlListGivenInterval(int interval, Date startDate) {
    for (int i = 1; i <= interval; i++) {
      dailyGameMethods.addGamesOfDay(urlList, startDate);
      startDate.incrementDate();
    }
    return urlList;
  }


  public List<String> getUrlList() {
      int i = startDate.daysInterval(endDate);
      return urlListGivenInterval(i, startDate);
  }

  public void removeFromUrlList(String url) {
    urlList.remove(url);
  }

}
