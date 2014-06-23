package jopc.d3calc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jodroid.d3calc.D3Prefs;
import jodroid.d3calc.R;
import jodroid.d3calc.cache.AsyncObjectHandler;
import jodroid.d3calc.cache.D3Cache;
import jodroid.d3calc.cache.D3CacheDesktop;
import jodroid.d3obj.D3Hero;
import jodroid.d3obj.D3HeroLite;
import jodroid.d3obj.D3HeroLiteDesktop;
import jodroid.d3obj.D3IconImpl;
import jodroid.d3obj.D3Item;
import jodroid.d3obj.D3ItemLite;
import jodroid.d3obj.D3Items;
import jodroid.d3obj.D3ItemsDesktop;
import jodroid.d3obj.D3Obj;
import jodroid.d3obj.D3Profile;
import jodroid.d3obj.D3ProfileLite;
import jodroid.d3obj.ID3Icon;
import jodroid.d3obj.ID3Skill;
import jopc.d3calc.D3ContextDesktop;
import android.util.Log;
import d3api.D3Url;

@SuppressWarnings("serial")
public class D3CalcDesktop extends JFrame implements ActionListener {

	private JButton btTest;
	private JLabel lblImg;
	private JComboBox<String> cbHost;
	private JTextField txtTag;
	private JList<D3ProfileLite> listProfiles;
	private JScrollPane panelLeft;
	private JSplitPane splitProfile;
	private JSplitPane splitHero;
	private JMenuItem itemDelete;
	private JMenuItem itemRefresh;
	
	private JList<D3HeroLite> listHeroes;
	private DefaultListModel<D3HeroLite> modelHeroes;
	
	private JList<D3ItemLite> listItems;
	private DefaultListModel<D3ItemLite> modelItems;
	
	private JList<ID3Skill> listSkills;
	private DefaultListModel<ID3Skill> modelSkills;
	
	private int popupIndex;

	private D3Cache cache;
	private boolean preferencesChanged = false;
	private D3Prefs generalPrefs;

	private boolean profilesChanged = false;
	private D3Profiles profiles;	// simple list without any real data
	private DefaultListModel<D3ProfileLite> modelProfiles; // full list with real data
	private D3Profile currentProfile;
	private D3Hero currentHero;
	private int itemCount;
	
	private boolean toggleTest;

	private String [] battlehosts = { "eu.battle.net", "us.battle.net" };

	public D3CalcDesktop() {
		// non graphic initializations
		cache = D3Cache.getInstance();
		generalPrefs = (D3Prefs)cache.xmlDecodeFromFile("generalPrefs.xml");
		if (generalPrefs == null) {
			generalPrefs = new D3Prefs();
			preferencesChanged = true;
		}
		System.out.println(generalPrefs.toString());
		profiles = (D3Profiles)cache.xmlDecodeFromFile("profiles.xml");
		if (profiles == null) {
			profiles = new D3Profiles();
			profilesChanged = true;
		}
		modelProfiles = new DefaultListModel<D3ProfileLite>();
		for (D3ProfileLite element : profiles.listProfiles)
			modelProfiles.addElement(element);

		// graphic
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setTitle("D3Calc");
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		Container cp = getContentPane();
		
		final JPopupMenu popmenu = new JPopupMenu("Profile");
		itemRefresh = new JMenuItem("Refresh");
		popmenu.add(itemRefresh);
		itemRefresh.addActionListener(this);
		itemDelete = new JMenuItem("Delete");
		popmenu.add(itemDelete);
		itemDelete.addActionListener(this);

		btTest = new JButton("Test");
		layout.putConstraint(SpringLayout.EAST, btTest, -10, SpringLayout.EAST, cp);
		layout.putConstraint(SpringLayout.NORTH, btTest, 10, SpringLayout.NORTH, cp);
		cp.add(btTest);
		btTest.addActionListener(this);

		lblImg = new JLabel("°");
		layout.putConstraint(SpringLayout.EAST, lblImg, -10, SpringLayout.EAST, cp);
		layout.putConstraint(SpringLayout.NORTH, lblImg, 10, SpringLayout.SOUTH, btTest);
		cp.add(lblImg);

		cbHost = new JComboBox<String>(battlehosts);
		layout.putConstraint(SpringLayout.WEST, cbHost, 10, SpringLayout.WEST, cp);
		layout.putConstraint(SpringLayout.NORTH, cbHost, 10, SpringLayout.NORTH, cp);
		cp.add(cbHost);

		txtTag = new JTextField(10);
		layout.putConstraint(SpringLayout.WEST, txtTag, 10, SpringLayout.EAST, cbHost);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, txtTag, 1, SpringLayout.VERTICAL_CENTER, cbHost);
		cp.add(txtTag);
		txtTag.addActionListener(this);

		listProfiles = new JList<D3ProfileLite>(modelProfiles);
//		listProfiles.setCellRenderer(new FocusedTitleListCellRenderer());
		listProfiles.setCellRenderer(new D3ProfileRenderer());
		listProfiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listProfiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me)) {
					popupIndex = listProfiles.locationToIndex(me.getPoint());
					if (popupIndex >= 0) {
						itemDelete.setText("Delete "+modelProfiles.get(popupIndex).battleTag.replace("-", "#"));
						popmenu.show(listProfiles,me.getX(), me.getY());
					}
				}
				if (SwingUtilities.isLeftMouseButton(me)) {
					popupIndex = listProfiles.locationToIndex(me.getPoint());
					if (popupIndex >= 0) {
						displayProfile(popupIndex, generalPrefs);
					}
				}
			}
		});
		panelLeft = new JScrollPane(listProfiles);

		splitProfile = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, new JPanel());
		layout.putConstraint(SpringLayout.WEST, splitProfile, 10, SpringLayout.WEST, cp);
		layout.putConstraint(SpringLayout.EAST, splitProfile, -10, SpringLayout.EAST, cp);
		layout.putConstraint(SpringLayout.NORTH, splitProfile, 10, SpringLayout.SOUTH, cbHost);
		layout.putConstraint(SpringLayout.SOUTH, splitProfile, -10, SpringLayout.SOUTH, cp);
		splitProfile.setDividerLocation(150);
		cp.add(splitProfile);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	if (preferencesChanged) cache.xmlEncodeToFile(generalPrefs, "generalPrefs.xml");
		    	if (profilesChanged) cache.xmlEncodeToFile(profiles, "profiles.xml");
		    }
		});
	}

	public static void main(String[] args) {

		D3Cache.getInstance().setDelegate(new D3CacheDesktop());
		D3Obj.setContext(new D3ContextDesktop());
		D3HeroLite.setDelegate(new D3HeroLiteDesktop());
		D3Items.setDelegate(new D3ItemsDesktop());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new D3CalcDesktop().setVisible(true);
			}
		});
	}

	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btTest) {
			if (toggleTest) {
				lblImg.setIcon(null);
			} else {
//				ID3Icon icon = cache.getItemIcon(D3Url.itemIconLarge2Url("amulet05_demonhunter_male"));
				D3IconImpl icon = (D3IconImpl)cache.getItemIcon(D3Url.itemIconLarge2Url("unique_mojo_011_104_witchdoctor_female"));
				lblImg.setIcon(new ImageIcon(icon.icon.image));
			}
			toggleTest = !toggleTest;
		}
		if (ae.getSource() == txtTag) {
			listProfiles.clearSelection();
			final D3ProfileLite profile = new D3ProfileLite(cbHost.getSelectedItem().toString(), txtTag.getText());
			if (modelProfiles.contains(profile)) return;
			cache.getProfile((String)cbHost.getSelectedItem(), txtTag.getText(), generalPrefs, new AsyncObjectHandler<D3Profile>() {

				@Override
				public void onSuccess(D3Profile prof) {
					profiles.add(profile);
					profilesChanged = true;
					modelProfiles.addElement(prof);
				}

				@Override
				public void onFailure(String code, String reason) {
					JOptionPane.showMessageDialog(D3CalcDesktop.this, "Error code="+code+"\nReason="+reason, "Profile loading error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		if (ae.getSource() == itemDelete) {
//			int ret = JOptionPane.showConfirmDialog(this, "Do you want to delete this profile ?\n"+modelProfiles.getElementAt(popupIndex), "Profile deletion", JOptionPane.YES_NO_OPTION);
			final String [] options = {"Delete", "Cancel operation"};
			int ret = JOptionPane.showOptionDialog(
					this,
					"Do you want to delete this profile ?\n"+modelProfiles.getElementAt(popupIndex),
					"Profile deletion",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					options,
					options[1]);
			if (ret == JOptionPane.YES_OPTION) {
				listProfiles.clearSelection();
				profiles.remove(popupIndex);
				profilesChanged = true;
				modelProfiles.remove(popupIndex);
			}
		}
		if (ae.getSource() == itemRefresh) {
			D3Prefs preftemp = generalPrefs.clone();
			preftemp.setLoadOnDemand(false); // force download from web
			displayProfile(popupIndex, preftemp);
		}
	}
	
	private void displayProfile(final int index, final D3Prefs prefs) {
		D3ProfileLite temp = modelProfiles.getElementAt(index);
		if (!(temp instanceof D3Profile)) {
			cache.getProfile(temp, prefs, new AsyncObjectHandler<D3Profile>() {
				@Override
				public void onSuccess(D3Profile prof) {
					modelProfiles.set(index, prof); // PROBLEM !! ça réappelle l'event (utiliser le mouse event click gauche ?)
					profiles.set(index, prof);
					displayProfile(index, prefs);
				}
				@Override
				public void onFailure(String code, String reason) {
					Log.w(D3CalcDesktop.class.getSimpleName(), "Error code = "+code+" ,reason="+reason);
				}
			});
		} else {
			currentProfile = (D3Profile)temp;
			Component comp = createProfileDetail(currentProfile);
			splitHero = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, comp, new JPanel());
			splitProfile.setRightComponent(splitHero);
		}
	}
	
	private Component createProfileDetail(D3Profile prof) {
		
		modelHeroes = new DefaultListModel<D3HeroLite>();
		for (D3HeroLite element : prof.heroes)
			modelHeroes.addElement(element);
		
		JPanel sp = new JPanel();
		SpringLayout sl = new SpringLayout();
		sp.setLayout(sl);
		sp.setPreferredSize(new Dimension(200, 50));
		JLabel label = new JLabel("Lifetime kills : "+prof.kills.monsters);
		sl.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, sp);
		sl.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, sp);
		sp.add(label);
		JLabel label2 = new JLabel("Elite Kills : "+prof.kills.elites);
		sl.putConstraint(SpringLayout.WEST, label2, 10, SpringLayout.WEST, sp);
		sl.putConstraint(SpringLayout.NORTH, label2, 10, SpringLayout.SOUTH, label);
		sp.add(label2);
		label = new JLabel("Last Updated : "+prof.getLastUpdated());
		sl.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, sp);
		sl.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, label2);
		sp.add(label);
		label2 = new JLabel("Last Save : "+prof.getLastSave());
		sl.putConstraint(SpringLayout.WEST, label2, 10, SpringLayout.WEST, sp);
		sl.putConstraint(SpringLayout.NORTH, label2, 10, SpringLayout.SOUTH, label);
		sp.add(label2);
		listHeroes = new JList<D3HeroLite>(modelHeroes);
		listHeroes.setCellRenderer(new D3HeroRenderer());
		listHeroes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane paneHeroes = new JScrollPane(listHeroes);
		sl.putConstraint(SpringLayout.WEST, paneHeroes, 10, SpringLayout.WEST, sp);
		sl.putConstraint(SpringLayout.EAST, paneHeroes, -10, SpringLayout.EAST, sp);
		sl.putConstraint(SpringLayout.NORTH, paneHeroes, 10, SpringLayout.SOUTH, label2);
		sl.putConstraint(SpringLayout.SOUTH, paneHeroes, -10, SpringLayout.SOUTH, sp);
		sp.add(paneHeroes);
		
		listHeroes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isLeftMouseButton(me)) {
					int index = listHeroes.locationToIndex(me.getPoint());
					if (index >= 0) {
						displayHero(index);
					}
				}
			}
		});
		return sp;
	}
	
	private void displayHero(final int index) {
		D3HeroLite temp = modelHeroes.getElementAt(index);
		if (!(temp instanceof D3Hero)) {
			cache.getHero(currentProfile.battlehost, currentProfile.battleTag, String.valueOf(temp.id), generalPrefs, new AsyncObjectHandler<D3Hero>() {

				@Override
				public void onSuccess(D3Hero obj) {
					modelHeroes.setElementAt(obj, index);
					currentProfile.heroes[index] = obj;
					displayHero(index);
				}

				@Override
				public void onFailure(String code, String reason) {
					Log.w(D3CalcDesktop.class.getSimpleName(), "Error code = "+code+" ,reason="+reason);
				}
			});
		} else {
			currentHero = (D3Hero)temp;
			itemCount = currentHero.items.itemArray.length;
			Log.i("displayHero", "hero name = "+currentHero.name+", nb item="+itemCount);
			JTabbedPane panel = new JTabbedPane(JTabbedPane.TOP);
			panel.add("Stats", new JScrollPane(createHeroStats(currentHero)));
			panel.add("Items", new JScrollPane(createHeroItems(currentHero)));
			panel.add("Skills", new JScrollPane(createHeroSkills(currentHero)));
			splitHero.setRightComponent(panel);
			for (int i = 0;i<currentHero.items.itemArray.length;i++) {
				updateItem(i);
			}
			listItems.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (SwingUtilities.isLeftMouseButton(me)) {
						int index = listItems.locationToIndex(me.getPoint());
						if (index >= 0) {
							D3ItemLite temp = modelItems.getElementAt(index);
							System.out.println(temp.toString());
						}
					}
				}
			});
		}
	}
	
	private Component buildSection(D3Hero hero, String title, int resLabelNames, int resFieldNames) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel names = new JPanel();
		names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
		JPanel values = new JPanel();
		values.setLayout(new BoxLayout(values, BoxLayout.Y_AXIS));
		String [] fieldNames = D3Obj.getContext().getStringArray(resFieldNames);
		int i = 0;
		for (String s : D3Obj.getContext().getStringArray(resLabelNames)) {
			names.add(new JLabel(s));
			values.add(new JLabel(hero.stats.getFieldByName(fieldNames[i++])));
		}
		panel.add(new JLabel(title), BorderLayout.NORTH);
		panel.add(names, BorderLayout.WEST);
		panel.add(values, BorderLayout.EAST);
		return panel;
	}
	
	private Component createHeroStats(D3Hero hero) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel stat = new JPanel();
		stat.setLayout(new BorderLayout());
		stat.add(new JLabel("STATS"), BorderLayout.NORTH);
		JPanel statLeft = new JPanel();
		statLeft.setLayout(new BoxLayout(statLeft, BoxLayout.Y_AXIS));
		for (String s : D3Obj.getContext().getStringArray(R.array.HeroStatsNames)) {
			statLeft.add(new JLabel(s));
		}
		stat.add(statLeft, BorderLayout.WEST);
		JPanel statRight = new JPanel();
		statRight.setLayout(new BoxLayout(statRight, BoxLayout.Y_AXIS));
		statRight.add(new JLabel(""+hero.level));
		statRight.add(new JLabel(""+hero.paragonLevel));
		statRight.add(new JLabel(""+hero.kills.elites));
		statRight.add(new JLabel(""+hero.getLastUpdated()));
		stat.add(statRight, BorderLayout.EAST);
		panel.add(stat);
		
		Component comp = buildSection(hero, "ATTRIBUTES", R.array.HeroAttributesNames, R.array.HeroAttributesFields);
		panel.add(comp);
		comp = buildSection(hero, "OFFENSE", R.array.HeroOffenseNames, R.array.HeroOffenseFields);
		panel.add(comp);
		comp = buildSection(hero, "DEFENSE", R.array.HeroDefenseNames, R.array.HeroDefenseFields);
		panel.add(comp);
		comp = buildSection(hero, "LIFE", R.array.HeroLifeNames, R.array.HeroLifeFields);
		panel.add(comp);
		return panel;
	}
	
	private Component createHeroItems(D3Hero hero) {
		modelItems = new DefaultListModel<D3ItemLite>();
		for (D3ItemLite element : hero.items.itemArray)
			modelItems.addElement(element);
		listItems = new JList<D3ItemLite>(modelItems);
		listItems.setCellRenderer(new D3ItemRenderer());
		listItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return listItems;
	}
	
	private Component createHeroSkills(D3Hero hero) {
		modelSkills = new DefaultListModel<ID3Skill>();
		for (ID3Skill element : hero.skills.active) {
			modelSkills.addElement(element);
			updateSkillIcon(element);
		}
		for (ID3Skill element : hero.skills.passive) {
			modelSkills.addElement(element);
			updateSkillIcon(element);
		}
		listSkills = new JList<ID3Skill>(modelSkills);
		listSkills.setCellRenderer(new D3SkillRenderer());
		listSkills.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return listSkills;
	}
	
	private void updateItem(int _index) {
		final int index = _index;
		final D3ItemLite temp = currentHero.items.itemArray[index];
		if (!(temp instanceof D3Item)) {
			cache.getItem(D3Url.item2Url(temp), new AsyncObjectHandler<D3Item>() {

				@Override
				public void onSuccess(D3Item item) {
					currentHero.items.itemArray[index] = item;
					modelItems.setElementAt(item, index);
					itemCount--;
					if (itemCount == 0) cache.writeHero(currentHero);
					updateIcon(index);
				}

				@Override
				public void onFailure(String code, String reason) {
					Log.w(D3CalcDesktop.class.getSimpleName(), "Item["+index+","+temp.itemSlot+"] Error code = "+code+" ,reason="+reason);
					itemCount--;
					if (itemCount == 0) cache.writeHero(currentHero);
				}
			});
		} else {
			if (((D3Item)temp).iconSmall == null) {
				updateIcon(index);
			}
		}
	}
	
	private void updateIcon(int _index) {
		final int index = _index;
		final D3ItemLite temp = currentHero.items.itemArray[index];
		if (temp instanceof D3Item) {
			D3Item item = (D3Item)temp;
			if (item.iconSmall == null) {
				ID3Icon icon = cache.getItemIcon(D3Url.itemIconSmall2Url(item.icon));
				item.iconSmall = icon;
			}
		}
	}
	
	private void updateSkillIcon(final ID3Skill skill) {
		if (skill.getSkill() == null) return;
		if (skill.getSkill().iconSmall == null) {
			ID3Icon icon = cache.getItemIcon(D3Url.skillIconSmall2Url(skill.getSkill().icon));
			skill.getSkill().iconSmall = icon;
		}
	}
}

/**
 * Just a little try with an example renderer
 */
class FocusedTitleListCellRenderer implements ListCellRenderer<D3ProfileLite> {
	protected static Border noFocusBorder = new EmptyBorder(15, 1, 1, 1);

	protected static TitledBorder focusBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
			"title");

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends D3ProfileLite> list, D3ProfileLite value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value.battleTag, index,
				isSelected, cellHasFocus);
		focusBorder.setTitle(value.battlehost);
		renderer.setBorder(cellHasFocus ? focusBorder : noFocusBorder);
		return renderer;
	}
}

