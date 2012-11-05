package org.mifosng.platform.api.data;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class StaffAccountSummaryCollectionData {

	@SuppressWarnings("unused")
	private final List<ClientSummary> clients;
	@SuppressWarnings("unused")
	private final List<GroupSummary> groups;

	public StaffAccountSummaryCollectionData(final List<ClientSummary> clients, final List<GroupSummary> groups) {
		this.clients = clients;
		this.groups = groups;
	}

	public static final class ClientSummary {

		private final Long id;
		private final String displayName;

		private Collection<ClientAccountSummaryData> loans;

		public ClientSummary(Long id, String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		public Long getId() {
			return id;
		}

		public String getDisplayName() {
			return displayName;
		}

		public Collection<ClientAccountSummaryData> getLoans() {
			return loans;
		}

		public void setLoans(Collection<ClientAccountSummaryData> loans) {
			this.loans = loans;
		}
	}

	public static final class GroupSummary {

		private final Long id;
		private final String name;

		private Collection<GroupAccountSummaryData> loans;

		public GroupSummary(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Collection<GroupAccountSummaryData> getLoans() {
			return loans;
		}

		public void setLoans(Collection<GroupAccountSummaryData> loans) {
			this.loans = loans;
		}
	}
}