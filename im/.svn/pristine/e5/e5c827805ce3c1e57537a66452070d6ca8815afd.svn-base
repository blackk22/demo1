package com.wonhigh.im.listener;

import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import com.wonhigh.im.db.RosterConstants;
import com.wonhigh.im.manager.IMDBManager;
import com.wonhigh.im.manager.IMManager;
import com.wonhigh.im.service.IMMainService;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMXmppUtil;

/**
 * TODO: 好友变更监听
 * 
 * @author USER
 * @date 2014-10-11 上午11:03:55
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMRosterListener implements RosterListener {

	private static final String TAG = "IMRosterListener";

	private IMMainService imMainService;

	private ContentResolver contentResolver;

	private Roster roster;

	private boolean isFristRoster;

	private String loginAccount;//登陆账号

	public IMRosterListener(IMMainService imMainService, Roster roster) {

		this.imMainService = imMainService;
		if (imMainService == null) {
			IMLogger.d(TAG, "imMainService==null");
		}

		contentResolver = imMainService.getContentResolver();

		this.roster = roster;
	}

	@Override
	public void entriesAdded(Collection<String> entries) {//成功登陆后调用
		IMLogger.d(TAG, "entriesAdded(" + entries + ")");
		ContentValues[] cvs = new ContentValues[entries.size()];   
		int i = 0;
		for (String entry : entries) {
			RosterEntry rosterEntry = roster.getEntry(entry);
			ContentValues values = IMXmppUtil.getContentValuesForRosterEntry(rosterEntry, roster,
					IMXmppUtil.getAccount(imMainService));
			cvs[i++] = values;

		}
		contentResolver.bulkInsert(RosterConstants.ROSTER_URI, cvs);
		if (isFristRoster) {
			isFristRoster = false;
			imMainService.rosterChanged();
		}
	}

	@Override
	public void entriesDeleted(Collection<String> entries) {
		IMLogger.d(TAG, "entriesDeleted(" + entries + ")");
		for (String entry : entries) {
			deleteRosterEntryFromDB(entry);
		}
		imMainService.rosterChanged();
	}

	@Override
	public void entriesUpdated(Collection<String> entries) {//重联成功后调用
		IMLogger.d(TAG, "entriesUpdated(" + entries + ")");
		for (String entry : entries) {
			RosterEntry rosterEntry = roster.getEntry(entry);
			updateRosterEntryInDB(rosterEntry);
		}
		imMainService.rosterChanged();
	}

	@Override
	public void presenceChanged(Presence presence) {//登陆状态发生变化时调用

		IMLogger.d(TAG, "presenceChanged(" + presence.getFrom() + "): " + presence);
		String jabberID = IMXmppUtil.getJid(presence.getFrom());
		RosterEntry rosterEntry = roster.getEntry(jabberID);
		updateRosterEntryInDB(rosterEntry);
		imMainService.rosterChanged();
	}

	private void addRosterEntryToDB(RosterEntry entry) {
		ContentValues values = IMXmppUtil.getContentValuesForRosterEntry(entry, roster,
				IMXmppUtil.getAccount(imMainService));
		Uri uri = contentResolver.insert(RosterConstants.ROSTER_URI, values);
		IMLogger.d(TAG, "addRosterEntryToDB: Inserted " + uri);
	}

	private void updateRosterEntryInDB(final RosterEntry entry) {
		ContentValues values = IMXmppUtil.getContentValuesForRosterEntry(entry, roster,
				IMXmppUtil.getAccount(imMainService));
//		String where = RosterConstants.ACCOUNT + " = ? and" + RosterConstants.JID + " = ?";
		if (contentResolver.update(RosterConstants.ROSTER_URI, values, RosterConstants.JID + " = ?", new String[] {
				entry.getUser() }) == 0)
			addRosterEntryToDB(entry);
	}

	private void deleteRosterEntryFromDB(final String jabberID) {
		int count = contentResolver.delete(RosterConstants.ROSTER_URI, RosterConstants.JID + " = ?",
				new String[] { jabberID });
		IMLogger.d(TAG, "deleteRosterEntryFromDB: Deleted " + count + " entries");
	}

}
