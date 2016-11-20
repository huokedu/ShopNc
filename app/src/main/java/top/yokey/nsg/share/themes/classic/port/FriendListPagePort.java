package top.yokey.nsg.share.themes.classic.port;

import top.yokey.nsg.share.OnekeyShareThemeImpl;
import top.yokey.nsg.share.themes.classic.FriendListPage;

@SuppressWarnings("all")
public class FriendListPagePort extends FriendListPage {

    private static final int DESIGN_SCREEN_WIDTH = 720;
    private static final int DESIGN_TITLE_HEIGHT = 96;

    public FriendListPagePort(OnekeyShareThemeImpl impl) {
        super(impl);
    }

    protected float getRatio() {
        float screenWidth = com.mob.tools.utils.R.getScreenWidth(activity);
        return screenWidth / DESIGN_SCREEN_WIDTH;
    }

    protected int getDesignTitleHeight() {
        return DESIGN_TITLE_HEIGHT;
    }

}