package accounts.apiobjects;

public class SingleSeasonGW2Rating {
    BestGW2AccountRating best;
    CurrentGW2AccountRating current;
    String season_id;

    public BestGW2AccountRating getBest() {
        return best;
    }

    public void setBest(BestGW2AccountRating best) {
        this.best = best;
    }

    public CurrentGW2AccountRating getCurrent() {
        return current;
    }

    public void setCurrent(CurrentGW2AccountRating current) {
        this.current = current;
    }

    public String getSeason_id() {
        return season_id;
    }

    public void setSeason_id(String season_id) {
        this.season_id = season_id;
    }
}
