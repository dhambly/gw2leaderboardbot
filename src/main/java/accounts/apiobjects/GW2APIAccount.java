package accounts.apiobjects;

public class GW2APIAccount {
    private int age;
    private int world;
    private String[] guilds;
    private String[] guild_leader;
    private String created;
    private String[] access;
    private boolean commander;
    private int fractal_level;
    private int daily_ap;
    private int monthly_ap;
    private int wvw_rank;
    private String id;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public String[] getGuilds() {
        return guilds;
    }

    public void setGuilds(String[] guilds) {
        this.guilds = guilds;
    }

    public String[] getGuild_leader() {
        return guild_leader;
    }

    public void setGuild_leader(String[] guild_leader) {
        this.guild_leader = guild_leader;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String[] getAccess() {
        return access;
    }

    public void setAccess(String[] access) {
        this.access = access;
    }

    public boolean isCommander() {
        return commander;
    }

    public void setCommander(boolean commander) {
        this.commander = commander;
    }

    public int getFractal_level() {
        return fractal_level;
    }

    public void setFractal_level(int fractal_level) {
        this.fractal_level = fractal_level;
    }

    public int getDaily_ap() {
        return daily_ap;
    }

    public void setDaily_ap(int daily_ap) {
        this.daily_ap = daily_ap;
    }

    public int getMonthly_ap() {
        return monthly_ap;
    }

    public void setMonthly_ap(int monthly_ap) {
        this.monthly_ap = monthly_ap;
    }

    public int getWvw_rank() {
        return wvw_rank;
    }

    public void setWvw_rank(int wvw_rank) {
        this.wvw_rank = wvw_rank;
    }
}
