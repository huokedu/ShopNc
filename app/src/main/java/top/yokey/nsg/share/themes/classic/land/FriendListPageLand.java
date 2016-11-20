package top.yokey.nsg.share.themes.classic.land;

import top.yokey.nsg.share.OnekeyShareThemeImpl;
import top.yokey.nsg.share.themes.classic.FriendListPage;

@SuppressWarnings("all")
public class FriendListPageLand extends FriendListPage {

    private static final int DESIGN_SCREEN_WIDTH = 1280;
    private static final int DESIGN_TITLE_HEIGHT = 70;

    public FriendListPageLand(OnekeyShareThemeImpl impl) {
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