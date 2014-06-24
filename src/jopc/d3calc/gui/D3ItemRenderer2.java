package jopc.d3calc.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jodroid.d3calc.R;
import jodroid.d3obj.D3Item;
import jodroid.d3obj.D3ItemAttribute;
import jodroid.d3obj.D3ItemLite;
import jodroid.d3obj.D3Obj;


/**
 * Unused.
 * @author JRD
 */
@SuppressWarnings("serial")
public class D3ItemRenderer2 extends JPanel implements
		ListCellRenderer<D3ItemLite> {

	protected static Border focusBorder = new BevelBorder(BevelBorder.RAISED);
	
	protected static TitledBorder titledBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
			"title");
	
	private JTextArea txtAttributes;
	
	private JLabel lblDesc;

	public D3ItemRenderer2() {
		setOpaque(true);
		this.setAlignmentX(LEFT_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		lblDesc = new JLabel();
		lblDesc.setFont(new Font("Serif", Font.ITALIC, 14));
		add(lblDesc);
		txtAttributes = new JTextArea();
		txtAttributes.setFont(new Font("Serif", Font.ITALIC, 12));
		add(txtAttributes);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends D3ItemLite> list, D3ItemLite value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		titledBorder.setTitle(value.name);
		setBorder(titledBorder);
		
		String strDps = new String();
		String strArmor = new String();
		txtAttributes.setText("");
		if (value instanceof D3Item) {
			D3Item item = (D3Item)value;
			if (item.dps != null) {
				double dps = item.dps.min;
				if (dps > 0) strDps = String.format("%.1f", dps)+" DPS";
			}
			if (item.attributesRaw.armorItem != null) {
				long armor = (long)(item.attributesRaw.armorItem.max + (item.attributesRaw.armorBonusItem != null ? item.attributesRaw.armorBonusItem.max : 0));
				strArmor = D3Obj.getContext().getString(R.string.item_armor)+" "+armor;
			}
			if (item.attributes.primary != null) {
				txtAttributes.append(D3Obj.getContext().getString(R.string.primary));
				for (D3ItemAttribute i : item.attributes.primary)
					txtAttributes.append("\n"+i.text);
			}
			if (item.attributes.secondary != null) {
				txtAttributes.append(D3Obj.getContext().getString(R.string.secondary));
				for (D3ItemAttribute i : item.attributes.secondary)
					txtAttributes.append("\n"+i.text);
			}
			if (item.attributes.passive != null) {
				txtAttributes.append(D3Obj.getContext().getString(R.string.passive));
				for (D3ItemAttribute i : item.attributes.passive)
					txtAttributes.append("\n"+i.text);
			}
			setPreferredSize(new Dimension(100, 10+(11*2*(item.attributes.NbAttributes()+3))));
		}
		String strDesc = value.itemSlot;
		if (!strDps.isEmpty()) strDesc += " , "+strDps;
		if (!strArmor.isEmpty()) strDesc += " , "+strArmor;
		lblDesc.setText(strDesc);
		
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
