package cl.koritsu.valued.event;

import java.util.Collection;

import cl.koritsu.valued.domain.Transaction;
import cl.koritsu.valued.view.ValuedViewType;

/*
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class ValuedEvent {

    public static final class UserLoginRequestedEvent {
        private final String userName, password;

        public UserLoginRequestedEvent(final String userName,
                final String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class BrowserResizeEvent {

    }

    public static class UserLoggedOutEvent {

    }

    public static class NotificationsCountUpdatedEvent {
    }

    public static final class ReportsCountUpdatedEvent {
        private final int count;

        public ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

    }

    public static final class TransactionReportEvent {
        private final Collection<Transaction> transactions;

        public TransactionReportEvent(final Collection<Transaction> transactions) {
            this.transactions = transactions;
        }

        public Collection<Transaction> getTransactions() {
            return transactions;
        }
    }

    public static final class PostViewChangeEvent {
        private final ValuedViewType view;

        public PostViewChangeEvent(final ValuedViewType view) {
            this.view = view;
        }

        public ValuedViewType getView() {
            return view;
        }
    }

    public static class CloseOpenWindowsEvent {
    }

    public static class ProfileUpdatedEvent {
    }

}
