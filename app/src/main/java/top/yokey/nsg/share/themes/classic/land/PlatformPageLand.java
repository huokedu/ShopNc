package top.yokey.nsg.share.themes.classic.land;

import java.util.ArrayList;

import top.yokey.nsg.share.OnekeyShareThemeImpl;
import top.yokey.nsg.share.themes.classic.PlatformPage;
import top.yokey.nsg.share.themes.classic.PlatformPageAdapter;

@SuppressWarnings("all")
public class PlatformPageLand extends PlatformPage {

	public PlatformPageLand(OnekeyShareThemeImpl impl) {
		super(impl);
	}

	public void onCreate() {
		requestLandscapeOrientation();
		super.onCreate();
	}

	protected PlatformPageAdapter newAdapter(ArrayList<Object> cells) {
		return new PlatformPageAdapterLand(this, cells);
	}

}