package codeOrchestra.actionScript.liveCoding.listener;

import codeOrchestra.actionScript.liveCoding.LiveCodingSession;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingListener {
  void onSessionStart(LiveCodingSession session);
  void onSessionEnd(LiveCodingSession session);
  void onSessionPause();
  void onSessionResume();
  void onAutoPausedSessionResume();
}
