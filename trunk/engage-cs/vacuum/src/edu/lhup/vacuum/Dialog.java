package edu.lhup.vacuum;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

abstract class Dialog extends JDialog implements ActionListener
{
	public final void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == m_ok)
		{
			if (handleOk())
			{
				setVisible(false);
				m_bOk = true;
			}
		}
		else if (evt.getSource() == m_cancel)
		{
			m_bOk = false;
			setVisible(false);
		}
	}

	public void setVisible(boolean bVisible)
	{
		m_bOk = false;
		super.setVisible(bVisible);
	}

	public final boolean isOk()
	{ return m_bOk; }

	abstract protected boolean handleOk();

	protected Dialog(Frame parent, String strTitle)
	{
		super(parent, strTitle, true);
		setResizable(true);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		m_ok.addActionListener(this);
		m_cancel.addActionListener(this);

		m_ok.setPreferredSize(new Dimension(80, 25));
		m_cancel.setPreferredSize(new Dimension(80, 25));

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		buttons.add(m_ok);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(m_cancel);
		buttons.setBorder(getPaddedBorder());

		getContentPane().add(buttons, BorderLayout.SOUTH); 


	}

	protected final void center()
	{
		Point screenLocation = getParent().getLocationOnScreen();
		Dimension parentSize = getParent().getSize();
		Dimension dialogSize = getSize();

		double dialogLeft = ((parentSize.getWidth() - dialogSize.getWidth()) / 2.0);
		double dialogTop = ((parentSize.getHeight() - dialogSize.getHeight()) / 2.0);

		setLocation((int)dialogLeft + (int)screenLocation.getX(), 
					(int)dialogTop + (int)screenLocation.getY());
	}

	protected final static Border getPaddedBorder()
	{
		return m_paddedBorder;
	}

	protected final static Border getLineBorder()
	{
		return m_lineBorder;
	}
		
	private boolean m_bOk = false;
	private final JButton m_ok = new JButton("OK");
	private final JButton m_cancel = new JButton("Cancel");
	private static final Border m_paddedBorder = BorderFactory.createEmptyBorder(5,5,5,5);
	private static final Border m_lineBorder = BorderFactory.createLineBorder(Color.black);
}
