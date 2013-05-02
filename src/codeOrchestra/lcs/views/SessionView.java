package codeOrchestra.lcs.views;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;
import codeOrchestra.lcs.session.LiveCodingManager;
import codeOrchestra.lcs.session.SessionViewManager;

public class SessionView extends ViewPart {
  
  public static final String ID = "LCS.session";
  
  private LiveCodingSession session;

  private MessagesTable messagesTable;
  
  @Override
  public void init(IViewSite site) throws PartInitException {
    super.init(site);
    
    String clientId = SessionViewManager.getInstance().getLastClientId();
    this.session = LiveCodingManager.instance().getSession(clientId);
    
    setPartName("Session #" + session.getSessionNumber());
    
    SessionViewManager.getInstance().reportViewCreated(clientId, this);
  }

  @Override
  public void createPartControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    parent.setLayout(layout);
    
    Group targetSettingsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
    targetSettingsGroup.setText("Client Info");
    GridLayout targetSettingsLayout = new GridLayout(2, false);
    targetSettingsLayout.marginHeight = 5;
    targetSettingsLayout.marginWidth = 5;
    targetSettingsGroup.setLayout(targetSettingsLayout);
    targetSettingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    
    Map<String, String> clientInfo = session.getClientInfo();    
    
    Label playerVersionLabel = new Label(targetSettingsGroup, SWT.NONE);
    playerVersionLabel.setText("Player version:");
    Label playerVersionValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    playerVersionValueLabel.setText(clientInfo.get("V"));
    
    Label playerTypeLabel = new Label(targetSettingsGroup, SWT.NONE);
    playerTypeLabel.setText("Player type:");
    Label playerTypeValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    playerTypeValueLabel.setText(clientInfo.get("PT"));
    
    Label manufacturerLabel = new Label(targetSettingsGroup, SWT.NONE);
    manufacturerLabel.setText("Manufacturer:");
    Label manufacturerValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    manufacturerValueLabel.setText(clientInfo.get("M"));
    
    Label osLabel = new Label(targetSettingsGroup, SWT.NONE);
    osLabel.setText("Operation system:");
    Label osValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    osValueLabel.setText(clientInfo.get("OS"));
        
    Label architectureLabel = new Label(targetSettingsGroup, SWT.NONE);
    architectureLabel.setText("System architecture:");
    Label architectureValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    architectureValueLabel.setText(clientInfo.get("ARCH"));
    
    Label languageLabel = new Label(targetSettingsGroup, SWT.NONE);
    languageLabel.setText("Language:");
    Label languageValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    languageValueLabel.setText(clientInfo.get("L"));
    
    Label resolutionLabel = new Label(targetSettingsGroup, SWT.NONE);
    resolutionLabel.setText("Resolution:");
    Label resolutionValueLabel = new Label(targetSettingsGroup, SWT.NONE);
    resolutionValueLabel.setText(clientInfo.get("R"));
    
    Composite messagesComposite = new Composite(parent, SWT.NONE);
    GridData messagesCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        
    GridLayout messagesCompositeLayout = new GridLayout(2, false);
    messagesCompositeLayout.marginHeight = 0;
    messagesCompositeLayout.marginWidth = 0;
    messagesCompositeLayout.horizontalSpacing = 0;
    messagesCompositeLayout.verticalSpacing = 0;
    messagesComposite.setLayout(messagesCompositeLayout);
    messagesComposite.setLayoutData(messagesCompositeGridData);
    
    messagesTable = new MessagesTable(messagesComposite, SWT.NONE); 
  }

  @Override
  public void setFocus() {
  }

}
