package jopc.d3calc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jodroid.d3calc.R;
import jodroid.d3obj.D3IconDesktop;
import jodroid.d3obj.D3Item;
import jodroid.d3obj.D3ItemAttribute;
import jodroid.d3obj.D3ItemLite;
import jodroid.d3obj.D3Obj;

@SuppressWarnings("serial")
public class D3ItemRenderer extends JPanel implements
		ListCellRenderer<D3ItemLite> {

	protected static Border focusBorder = new BevelBorder(BevelBorder.RAISED);
	
	protected static TitledBorder titledBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
			"title");
	
	private JLabel lblIcon;
	private JLabel lblSlot;
	private JLabel lblDPS;
	private JLabel lblArmor;
	private JTextArea txtAttributes;

	public D3ItemRenderer() {
		setOpaque(true);
		SpringLayout sp = new SpringLayout();
		setLayout(sp);
		
		setPreferredSize(new Dimension(100,50));
		
		lblSlot = new JLabel();
		lblSlot.setFont(new Font("Serif", Font.BOLD, 12));
		sp.putConstraint(SpringLayout.WEST, lblSlot, 2, SpringLayout.WEST, this);
		sp.putConstraint(SpringLayout.NORTH, lblSlot, -2, SpringLayout.NORTH, this);
		add(lblSlot);
		
		lblDPS = new JLabel();
		lblDPS.setFont(new Font("Serif", Font.BOLD, 11));
		sp.putConstraint(SpringLayout.EAST, lblDPS, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.NORTH, lblDPS, -2, SpringLayout.NORTH, this);
		add(lblDPS);
		
		lblArmor = new JLabel();
		lblArmor.setFont(new Font("Serif", Font.BOLD, 11));
//		lblArmor.setForeground(Color.BLUE);
		sp.putConstraint(SpringLayout.NORTH, lblArmor, -2, SpringLayout.SOUTH, lblDPS);
		sp.putConstraint(SpringLayout.EAST, lblArmor, -2, SpringLayout.EAST, this);
		add(lblArmor);
		
		lblIcon = new JLabel();
		sp.putConstraint(SpringLayout.WEST, lblIcon, 2, SpringLayout.WEST, this);
		sp.putConstraint(SpringLayout.NORTH, lblIcon, 2, SpringLayout.SOUTH, lblSlot);
		add(lblIcon);
		
		txtAttributes = new JTextArea();
		txtAttributes.setFont(new Font("Serif", Font.ITALIC, 11));
		sp.putConstraint(SpringLayout.WEST, txtAttributes, 2, SpringLayout.EAST, lblIcon);
		sp.putConstraint(SpringLayout.NORTH, txtAttributes, 2, SpringLayout.SOUTH, lblSlot);
		add(txtAttributes);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends D3ItemLite> list, D3ItemLite value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		titledBorder.setTitle(value.name);
		titledBorder.setTitleColor(new Color(D3Obj.getColor(value.displayColor, R.color.black)));
		setBorder(titledBorder);
		lblSlot.setText(value.itemSlot);
		
		String strDps = "";
		String strArmor = "";
		txtAttributes.setText("");
		if (value instanceof D3Item) {
			D3Item item = (D3Item)value;
			if (item.dps != null) {
				double dps = item.dps.min;
				if (dps > 0) strDps = String.format("%.1f", dps)+" DPS";
			}
			if (item.armor != null) { // new style
				strArmor = D3Obj.getContext().getString(R.string.item_armor)+" "+item.armor.max;
			} else if (item.attributesRaw.armorItem != null) { // old style
				long armor = (long)(item.attributesRaw.armorItem.max + (item.attributesRaw.armorBonusItem != null ? item.attributesRaw.armorBonusItem.max : 0));
				strArmor = D3Obj.getContext().getString(R.string.item_armor)+" "+armor;
			}
			if (item.attributes.primary != null) {
				if (item.attributes.primary.length != 0)
					txtAttributes.append(D3Obj.getContext().getString(R.string.primary));
				for (D3ItemAttribute i : item.attributes.primary)
					txtAttributes.append("\n  "+i.text);
			}
			if (item.attributes.secondary != null) {
				if (item.attributes.secondary.length != 0)
					txtAttributes.append("\n"+D3Obj.getContext().getString(R.string.secondary));
				for (D3ItemAttribute i : item.attributes.secondary)
					txtAttributes.append("\n  "+i.text);
			}
			if (item.attributes.passive != null) {
				if (item.attributes.passive.length != 0)
					txtAttributes.append("\n"+D3Obj.getContext().getString(R.string.passive));
				for (D3ItemAttribute i : item.attributes.passive)
					txtAttributes.append("\n  "+i.text);
			}
			if (item.iconSmall != null) {
				D3IconDesktop icon = (D3IconDesktop)item.iconSmall;
				lblIcon.setIcon(new ImageIcon(icon.icon.image));
			}
			setPreferredSize(new Dimension(100, 40+15*(item.attributes.NbAttributes()+3)));
		}
		lblArmor.setText(strArmor);
		lblDPS.setText(strDps);
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			txtAttributes.setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			txtAttributes.setBackground(list.getBackground());
		}
//		setBorder(isSelected ? focusBorder : null);
		return this;
	}

}
