package com.shawnhu.seagull.seagull.twitter;

import com.shawnhu.seagull.utils.querybuilder.Columns;
import com.shawnhu.seagull.utils.querybuilder.OrderBy;
import com.shawnhu.seagull.utils.querybuilder.SQLQueryBuilder;
import com.shawnhu.seagull.utils.querybuilder.Selectable;
import com.shawnhu.seagull.utils.querybuilder.Tables;
import com.shawnhu.seagull.utils.querybuilder.Utils;
import com.shawnhu.seagull.utils.querybuilder.Where;
import com.shawnhu.seagull.utils.querybuilder.query.SQLSelectQuery;

public class TwitterQueryBuilder {

	public static final class ConversationQueryBuilder {

		public static final String buildByConversationId(final String[] projection, final long account_id,
				final long conversationId, final String selection, final String sortOrder) {
			final Selectable select = Utils.getColumnsFromProjection(projection);
			final SQLSelectQuery.Builder qb = SQLQueryBuilder.select(select);
			qb.from(new Tables(TweetStore.DirectMessages.TABLE_NAME));
			final Where accountIdWhere = Where.equals(TweetStore.DirectMessages.ACCOUNT_ID, account_id);
			final Where incomingWhere = Where.and(Where.notEquals(TweetStore.DirectMessages.IS_OUTGOING, 1),
					Where.equals(TweetStore.DirectMessages.SENDER_ID, conversationId));
			final Where outgoingWhere = Where.and(Where.equals(TweetStore.DirectMessages.IS_OUTGOING, 1),
					Where.equals(TweetStore.DirectMessages.RECIPIENT_ID, conversationId));
			final Where conversationWhere = Where.or(incomingWhere, outgoingWhere);
			if (selection != null) {
				qb.where(Where.and(accountIdWhere, conversationWhere, new Where(selection)));
			} else {
				qb.where(Where.and(accountIdWhere, conversationWhere));
			}
			qb.orderBy(new OrderBy(sortOrder != null ? sortOrder : TweetStore.DirectMessages.Conversation.DEFAULT_SORT_ORDER));
			return qb.build().getSQL();
		}

		public static final String buildByScreenName(final String[] projection, final long account_id,
				final String screen_name, final String selection, final String sortOrder) {
			final Selectable select = Utils.getColumnsFromProjection(projection);
			final SQLSelectQuery.Builder qb = SQLQueryBuilder.select(select);
			qb.select(select);
			qb.from(new Tables(TweetStore.DirectMessages.TABLE_NAME));
			final Where accountIdWhere = Where.equals(TweetStore.DirectMessages.ACCOUNT_ID, account_id);
			final Where incomingWhere = Where.and(Where.notEquals(TweetStore.DirectMessages.IS_OUTGOING, 1),
					Where.equals(new Columns.Column(TweetStore.DirectMessages.SENDER_SCREEN_NAME), screen_name));
			final Where outgoingWhere = Where.and(Where.equals(TweetStore.DirectMessages.IS_OUTGOING, 1),
					Where.equals(new Columns.Column(TweetStore.DirectMessages.RECIPIENT_SCREEN_NAME), screen_name));
			if (selection != null) {
				qb.where(Where.and(accountIdWhere, incomingWhere, outgoingWhere, new Where(selection)));
			} else {
				qb.where(Where.and(accountIdWhere, incomingWhere, outgoingWhere));
			}
			qb.orderBy(new OrderBy(sortOrder != null ? sortOrder : TweetStore.DirectMessages.Conversation.DEFAULT_SORT_ORDER));
			return qb.build().getSQL();
		}

	}

	public static class ConversationsEntryQueryBuilder {

		public static SQLSelectQuery build() {
			return build(null);
		}

		public static SQLSelectQuery build(final String selection) {
			final SQLSelectQuery.Builder qb = new SQLSelectQuery.Builder();
			qb.select(new Columns(new Columns.Column(TweetStore.DirectMessages._ID), new Columns.Column(TweetStore.DirectMessages.ConversationEntries.MESSAGE_TIMESTAMP),
					new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), new Columns.Column(TweetStore.DirectMessages.ACCOUNT_ID), new Columns.Column(
							TweetStore.DirectMessages.IS_OUTGOING), new Columns.Column(TweetStore.DirectMessages.ConversationEntries.NAME), new Columns.Column(
							TweetStore.DirectMessages.ConversationEntries.SCREEN_NAME), new Columns.Column(TweetStore.DirectMessages.ConversationEntries.PROFILE_IMAGE_URL),
					new Columns.Column(TweetStore.DirectMessages.ConversationEntries.TEXT_HTML), new Columns.Column(TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID)));
			final SQLSelectQuery.Builder entryIds = new SQLSelectQuery.Builder();
			entryIds.select(new Columns(new Columns.Column(TweetStore.DirectMessages._ID), new Columns.Column(
					TweetStore.DirectMessages.ConversationEntries.MESSAGE_TIMESTAMP), new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), new Columns.Column(
					TweetStore.DirectMessages.ACCOUNT_ID), new Columns.Column("0", TweetStore.DirectMessages.IS_OUTGOING), new Columns.Column(
					TweetStore.DirectMessages.SENDER_NAME, TweetStore.DirectMessages.ConversationEntries.NAME), new Columns.Column(
					TweetStore.DirectMessages.SENDER_SCREEN_NAME, TweetStore.DirectMessages.ConversationEntries.SCREEN_NAME), new Columns.Column(
					TweetStore.DirectMessages.SENDER_PROFILE_IMAGE_URL, TweetStore.DirectMessages.ConversationEntries.PROFILE_IMAGE_URL), new Columns.Column(
					TweetStore.DirectMessages.ConversationEntries.TEXT_HTML), new Columns.Column(TweetStore.DirectMessages.SENDER_ID,
					TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID)));
			entryIds.from(new Tables(TweetStore.DirectMessages.Inbox.TABLE_NAME));
			entryIds.union();
			entryIds.select(new Columns(new Columns.Column(TweetStore.DirectMessages._ID), new Columns.Column(
					TweetStore.DirectMessages.ConversationEntries.MESSAGE_TIMESTAMP), new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), new Columns.Column(
					TweetStore.DirectMessages.ACCOUNT_ID), new Columns.Column("1", TweetStore.DirectMessages.IS_OUTGOING), new Columns.Column(
					TweetStore.DirectMessages.RECIPIENT_NAME, TweetStore.DirectMessages.ConversationEntries.NAME), new Columns.Column(
					TweetStore.DirectMessages.RECIPIENT_SCREEN_NAME, TweetStore.DirectMessages.ConversationEntries.SCREEN_NAME), new Columns.Column(
					TweetStore.DirectMessages.RECIPIENT_PROFILE_IMAGE_URL, TweetStore.DirectMessages.ConversationEntries.PROFILE_IMAGE_URL), new Columns.Column(
					TweetStore.DirectMessages.ConversationEntries.TEXT_HTML), new Columns.Column(TweetStore.DirectMessages.RECIPIENT_ID,
					TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID)));
			entryIds.from(new Tables(TweetStore.DirectMessages.Outbox.TABLE_NAME));
			qb.from(entryIds.build());
			final SQLSelectQuery.Builder recent_inbox_msg_ids = SQLQueryBuilder
					.select(new Columns.Column("MAX(" + TweetStore.DirectMessages.MESSAGE_ID + ")")).from(new Tables(TweetStore.DirectMessages.Inbox.TABLE_NAME))
					.groupBy(new Columns.Column(TweetStore.DirectMessages.SENDER_ID));
			final SQLSelectQuery.Builder recent_outbox_msg_ids = SQLQueryBuilder
					.select(new Columns.Column("MAX(" + TweetStore.DirectMessages.MESSAGE_ID + ")")).from(new Tables(TweetStore.DirectMessages.Outbox.TABLE_NAME))
					.groupBy(new Columns.Column(TweetStore.DirectMessages.RECIPIENT_ID));
			final SQLSelectQuery.Builder conversationIds = new SQLSelectQuery.Builder();
			conversationIds.select(new Columns(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), new Columns.Column(
					TweetStore.DirectMessages.SENDER_ID, TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID)));
			conversationIds.from(new Tables(TweetStore.DirectMessages.Inbox.TABLE_NAME));
			conversationIds.where(Where.in(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), recent_inbox_msg_ids.build()));
			conversationIds.union();
			conversationIds.select(new Columns(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), new Columns.Column(
					TweetStore.DirectMessages.RECIPIENT_ID, TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID)));
			conversationIds.from(new Tables(TweetStore.DirectMessages.Outbox.TABLE_NAME));
			conversationIds.where(Where.in(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), recent_outbox_msg_ids.build()));
			final SQLSelectQuery.Builder groupedConversationIds = new SQLSelectQuery.Builder();
			groupedConversationIds.select(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID));
			groupedConversationIds.from(conversationIds.build());
			groupedConversationIds.groupBy(new Columns.Column(TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID));
			final Where groupedWhere = Where.in(new Columns.Column(TweetStore.DirectMessages.MESSAGE_ID), groupedConversationIds.build());
			final Where where;
			if (selection != null) {
				where = Where.and(groupedWhere, new Where(selection));
			} else {
				where = groupedWhere;
			}
			qb.where(where);
			qb.groupBy(Utils.getColumnsFromProjection(TweetStore.DirectMessages.ConversationEntries.CONVERSATION_ID, TweetStore.DirectMessages.ACCOUNT_ID));
			qb.orderBy(new OrderBy(TweetStore.DirectMessages.ConversationEntries.MESSAGE_TIMESTAMP + " DESC"));
			return qb.build();
		}

	}

	public static final class DirectMessagesQueryBuilder {

		public static final SQLSelectQuery build() {
			return build(null, null, null);
		}

		public static final SQLSelectQuery build(final String[] projection, final String selection,
				final String sortOrder) {
			final SQLSelectQuery.Builder qb = new SQLSelectQuery.Builder();
			final Selectable select = Utils.getColumnsFromProjection(projection);
			qb.select(select).from(new Tables(TweetStore.DirectMessages.Inbox.TABLE_NAME));
			if (selection != null) {
				qb.where(new Where(selection));
			}
			qb.union();
			qb.select(select).from(new Tables(TweetStore.DirectMessages.Outbox.TABLE_NAME));
			if (selection != null) {
				qb.where(new Where(selection));
			}
			qb.orderBy(new OrderBy(sortOrder != null ? sortOrder : TweetStore.DirectMessages.DEFAULT_SORT_ORDER));
			return qb.build();
		}

	}

}
