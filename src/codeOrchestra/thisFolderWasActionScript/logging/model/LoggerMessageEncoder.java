package codeOrchestra.actionScript.logging.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import codeOrchestra.actionScript.logging.model.LoggerMessage.Builder;
import codeOrchestra.lcs.logging.Level;
import codeOrchestra.utils.StringUtils;
import codeOrchestra.utils.XMLUtils;

/**
 * @author Alexander Eliseyev
 */
public final class LoggerMessageEncoder {

  private static final String LOG_MESSAGE_ELEMENT = "logMessage";
  private static final String SOURCE_ELEMENT = "source";
  private static final String NODE_ID_ATTRIBUTE = "nodeId";
  private static final String MODEL_REFERENCE_ATTRIBUTE = "modelReference";
  private static final String MESSAGE_ELEMENT = "message";
  private static final String SEVERITY_ATTRIBUTE = "severity";
  private static final String ROOT_ELEMENT = "root";
  private static final String SCOPES_ELEMENT = "scopes";
  private static final String SCOPE_ELEMENT = "scope";
  private static final String ID_ATTRIBUTE = "id";
  private static final String STACK_TRACE_ELEMENT = "stackTrace";
  private static final String TIMESTAMP_ELEMENT = "timestamp";

  private static final Level DEFAULT_LEVEL = Level.INFO;

  private static Map<String, Level> severityMap;

  static {
    severityMap = new HashMap<String, Level>();
    severityMap.put("fatal", Level.ERROR);
    severityMap.put("error", Level.ERROR);
    severityMap.put("warn", Level.WARN);
    severityMap.put("debug", Level.INFO);
    severityMap.put("info", Level.INFO);
    severityMap.put("trace", Level.INFO);
  }

  private static Level getLevel(String severity) {
    Level level = severityMap.get(severity);
    if (level == null) {
      level = DEFAULT_LEVEL;
    }
    return level;
  }

  public static boolean isLegitSeverityLevel(String severity) {
    return severityMap.get(severity) != null;
  }

  private LoggerMessageEncoder() {
  }

  public static LoggerMessage encode(String xmlString) {
    Document document = XMLUtils.stringToDOM(xmlString);
    Element logMessageRootElement = document.getDocumentElement();

    if (LOG_MESSAGE_ELEMENT.equals(document)) {
      throw new RuntimeException(LOG_MESSAGE_ELEMENT + " root element expected");
    }

    // Root FQ Name
    NodeList rootNodeList = logMessageRootElement.getElementsByTagName(ROOT_ELEMENT);
    String rootFQName = null;
    if (rootNodeList != null && rootNodeList.getLength() > 0) {
      Element rootElement = (Element) rootNodeList.item(0);
      rootFQName = rootElement.getTextContent();
    }

    // Timestamp
    NodeList timestampNodeList = logMessageRootElement.getElementsByTagName(TIMESTAMP_ELEMENT);
    if (timestampNodeList == null || timestampNodeList.getLength() != 1) {
      throw new RuntimeException("Exactly one " + TIMESTAMP_ELEMENT + " subelement expected");
    }
    Element timestampElement = (Element) timestampNodeList.item(0);
    String timestsampStr = timestampElement.getTextContent();

    // Source node model & node Id
    NodeList sourceNodeList = logMessageRootElement.getElementsByTagName(SOURCE_ELEMENT);
    if (sourceNodeList == null || sourceNodeList.getLength() != 1) {
      throw new RuntimeException("Exactly one " + SOURCE_ELEMENT + " subelement expected");
    }
    Element sourceElement = (Element) sourceNodeList.item(0);
    String nodeIdStr = sourceElement.getAttribute(NODE_ID_ATTRIBUTE);
    String modelReferenceStr = sourceElement.getAttribute(MODEL_REFERENCE_ATTRIBUTE);

    // Scopes
    List<LoggerScopeWrapper> scopesList = new ArrayList<LoggerScopeWrapper>();
    NodeList scopeRootElementNodeList = logMessageRootElement.getElementsByTagName(SCOPES_ELEMENT);
    if (scopeRootElementNodeList != null && scopeRootElementNodeList.getLength() > 0) {
      Element scopeRootElement = (Element) scopeRootElementNodeList.item(0);
      NodeList scopeNodeList = scopeRootElement.getElementsByTagName(SCOPE_ELEMENT);

      if (scopeNodeList != null && scopeNodeList.getLength() > 0) {
        for (int i = 0; i < scopeNodeList.getLength(); i++) {
          Element scopeElement = (Element) scopeNodeList.item(i);

          String scopeName = scopeElement.getTextContent();
          String scopeId = scopeElement.getAttribute(ID_ATTRIBUTE);

          scopesList.add(new LoggerScopeWrapper(scopeId, scopeName));
        }
      }
    }

    // Message content & severity
    NodeList messageNodeList = logMessageRootElement.getElementsByTagName(MESSAGE_ELEMENT);
    if (messageNodeList == null || messageNodeList.getLength() != 1) {
      throw new RuntimeException("Exactly one " + MESSAGE_ELEMENT + " subelement expected");
    }
    Element messageElement = (Element) messageNodeList.item(0);
    String command = messageElement.getAttribute(SEVERITY_ATTRIBUTE);
    if (StringUtils.isEmpty(command)) {
      throw new RuntimeException(SEVERITY_ATTRIBUTE + " " + MESSAGE_ELEMENT + " element attribute expected");
    }

    // Stack trace
    String stackTrace = null;
    NodeList stackTraceNodeList = logMessageRootElement.getElementsByTagName(STACK_TRACE_ELEMENT);
    if (stackTraceNodeList != null && stackTraceNodeList.getLength() == 1) {
      Element stackTraceElement = (Element) stackTraceNodeList.item(0);
      stackTrace = stackTraceElement.getTextContent();
    }

    // RE-2174
    String content = messageElement.getTextContent();
    if (content.startsWith("|")) {
      content = content.substring(1);
    }

    Level severityLevel = getLevel(command);
    Long timestamp = null;
    try {
      timestamp = Long.valueOf(timestsampStr);
    } catch (Throwable t) {
      throw new RuntimeException("Can't parse timestamp " + timestsampStr);
    }

    LoggerMessage message = new Builder()
      .command(command)
      .message(content)
      .rootFQName(rootFQName)
      .scopes(scopesList)
      .severity(severityLevel)
      .timestamp(timestamp)
      .stackTrace(stackTrace)
      .build();

    return message;
  }

}
