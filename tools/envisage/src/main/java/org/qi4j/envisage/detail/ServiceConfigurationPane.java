/*  Copyright 2009 Tonny Kohar.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.envisage.detail;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.qi4j.envisage.event.LinkEvent;
import org.qi4j.envisage.model.descriptor.CompositeDetailDescriptor;
import org.qi4j.envisage.model.descriptor.EntityDetailDescriptor;
import org.qi4j.envisage.model.descriptor.ObjectDetailDescriptor;
import org.qi4j.envisage.model.descriptor.ServiceDetailDescriptor;
import org.qi4j.envisage.model.descriptor.ValueDetailDescriptor;
import org.qi4j.envisage.model.util.DescriptorUtilities;
import org.qi4j.spi.object.ObjectDescriptor;

/**
 * Implementation of Service Configuration Panel
 */
public class ServiceConfigurationPane
    extends DetailPane
{
    protected ResourceBundle bundle = ResourceBundle.getBundle( this.getClass().getName() );

    private JPanel contentPane;
    private JLabel nameLabel;
    private JLabel classLabel;
    private JButton linkButton;
    private JLabel typeLabel;

    private Object configDescriptor;
    private Cursor defaultCursor;
    private Cursor linkCursor;

    public ServiceConfigurationPane( DetailModelPane detailModelPane )
    {
        super( detailModelPane );
        this.setLayout( new BorderLayout() );
        this.add( contentPane, BorderLayout.CENTER );

        defaultCursor = getCursor();
        linkCursor = LinkEvent.LINK_CURSOR;

        linkButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent evt )
            {
                linkActivated();
            }
        } );

        nameLabel.addMouseListener( new MouseAdapter()
        {
            public void mouseClicked( MouseEvent evt )
            {
                linkActivated();
            }

            public void mouseEntered( MouseEvent evt )
            {
                setCursor( linkCursor );
            }

            public void mouseExited( MouseEvent evt )
            {
                setCursor( defaultCursor );
            }
        } );
    }

    protected void linkActivated()
    {
        if( configDescriptor == null )
        {
            return;
        }
        LinkEvent linkEvt = new LinkEvent( this, configDescriptor );
        detailModelPane.fireLinkActivated( linkEvt );
    }

    protected void clear()
    {
        nameLabel.setText( null );
        classLabel.setText( null );
        typeLabel.setText( null );
        linkButton.setEnabled( false );
        configDescriptor = null;
    }

    public void setDescriptor( Object objectDesciptor )
    {
        clear();

        if( !( objectDesciptor instanceof ServiceDetailDescriptor ) )
        {
            return;
        }

        configDescriptor = DescriptorUtilities.findServiceConfiguration( (ServiceDetailDescriptor) objectDesciptor );
        if( configDescriptor == null )
        {
            return;
        }

        ObjectDescriptor spiDescriptor = null;
        String typeString = null;
        if( configDescriptor instanceof ServiceDetailDescriptor )
        {
            spiDescriptor = ( (ServiceDetailDescriptor) configDescriptor ).descriptor();
            typeString = "Service";
        }
        else if( configDescriptor instanceof EntityDetailDescriptor )
        {
            spiDescriptor = ( (EntityDetailDescriptor) configDescriptor ).descriptor();
            typeString = "Entity";
        }
        else if( configDescriptor instanceof ValueDetailDescriptor )
        {
            spiDescriptor = ( (ValueDetailDescriptor) configDescriptor ).descriptor();
            typeString = "Value";
        }
        else if( configDescriptor instanceof ObjectDetailDescriptor )
        {
            spiDescriptor = ( (ObjectDetailDescriptor) configDescriptor ).descriptor();
            typeString = "Object";
        }
        else if( configDescriptor instanceof CompositeDetailDescriptor )
        {
            spiDescriptor = ( (ObjectDetailDescriptor) configDescriptor ).descriptor();
            typeString = "Transient";
        }

        String simpleName = spiDescriptor.type().getSimpleName();
        nameLabel.setText( "<html><a href='" + simpleName + "'>" + simpleName + "</a></html>" );
        classLabel.setText( spiDescriptor.type().getName() );
        typeLabel.setText( typeString );
        linkButton.setEnabled( true );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        contentPane = new JPanel();
        contentPane.setLayout( new GridBagLayout() );
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$( label1, ResourceBundle.getBundle( "org/qi4j/envisage/detail/ServiceConfigurationPane" ).getString( "CTL_Name.Text" ) );
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        contentPane.add( label1, gbc );
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add( spacer1, gbc );
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add( spacer2, gbc );
        nameLabel = new JLabel();
        nameLabel.setText( "Label" );
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add( nameLabel, gbc );
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add( spacer3, gbc );
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$( label2, ResourceBundle.getBundle( "org/qi4j/envisage/detail/ServiceConfigurationPane" ).getString( "CTL_Class.Text" ) );
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        contentPane.add( label2, gbc );
        classLabel = new JLabel();
        classLabel.setText( "Label" );
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add( classLabel, gbc );
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add( spacer4, gbc );
        linkButton = new JButton();
        this.$$$loadButtonText$$$( linkButton, ResourceBundle.getBundle( "org/qi4j/envisage/detail/ServiceConfigurationPane" ).getString( "CTL_Link.Text" ) );
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add( linkButton, gbc );
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add( spacer5, gbc );
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$( label3, ResourceBundle.getBundle( "org/qi4j/envisage/detail/ServiceConfigurationPane" ).getString( "CTL_Type.Text" ) );
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        contentPane.add( label3, gbc );
        typeLabel = new JLabel();
        typeLabel.setText( "Label" );
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add( typeLabel, gbc );
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add( spacer6, gbc );
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$( JLabel component, String text )
    {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for( int i = 0; i < text.length(); i++ )
        {
            if( text.charAt( i ) == '&' )
            {
                i++;
                if( i == text.length() )
                {
                    break;
                }
                if( !haveMnemonic && text.charAt( i ) != '&' )
                {
                    haveMnemonic = true;
                    mnemonic = text.charAt( i );
                    mnemonicIndex = result.length();
                }
            }
            result.append( text.charAt( i ) );
        }
        component.setText( result.toString() );
        if( haveMnemonic )
        {
            component.setDisplayedMnemonic( mnemonic );
            component.setDisplayedMnemonicIndex( mnemonicIndex );
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$( AbstractButton component, String text )
    {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for( int i = 0; i < text.length(); i++ )
        {
            if( text.charAt( i ) == '&' )
            {
                i++;
                if( i == text.length() )
                {
                    break;
                }
                if( !haveMnemonic && text.charAt( i ) != '&' )
                {
                    haveMnemonic = true;
                    mnemonic = text.charAt( i );
                    mnemonicIndex = result.length();
                }
            }
            result.append( text.charAt( i ) );
        }
        component.setText( result.toString() );
        if( haveMnemonic )
        {
            component.setMnemonic( mnemonic );
            component.setDisplayedMnemonicIndex( mnemonicIndex );
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return contentPane;
    }
}