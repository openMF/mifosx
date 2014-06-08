package org.mifosplatform.batch.command;

public class CommandContext {

	public static class Builder{
		
		private String resource;
		private String method;
		
		private Builder(final String resource) {
			this.resource = resource;
		}
		
		public Builder method(final String method) {
			this.method = method;
			return this;
		}
		
		public CommandContext build() {
			return new CommandContext(this.resource, this.method);
		}
		
	}
	
	private final String resource;
	private final String method;
	
	private CommandContext(final String resource, final String method) {
	
		this.resource = resource;
		this.method = method;
	}
	
	public static Builder resource(final String resource) {
		return new Builder(resource);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.method == null) ? 0 : this.method.hashCode());
		result = prime * result
				+ ((this.resource == null) ? 0 : this.resource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommandContext other = (CommandContext) obj;
		if (this.method == null) {
			if (other.method != null)
				return false;
		} else if (!this.method.equals(other.method))
			return false;
		if (this.resource == null) {
			if (other.resource != null)
				return false;
		} else if (!this.resource.equals(other.resource))
			return false;
		return true;
	}
	
	
	
}
