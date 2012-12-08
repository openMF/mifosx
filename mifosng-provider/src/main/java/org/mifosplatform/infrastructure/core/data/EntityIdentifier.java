package org.mifosplatform.infrastructure.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the successful result of an REST API call.
 */
public class EntityIdentifier {

    private Long entityId;

    // TODO - Rename variable to commandId or taskId or something that shows
    // this is the id of a command in a table/queue for processing.
    @SuppressWarnings("unused")
    private Long makerCheckerId;

    private Map<String, Object> changes;

    public static EntityIdentifier makerChecker(final Long makerCheckerId) {
        return new EntityIdentifier(null, makerCheckerId, null);
    }

    public static EntityIdentifier makerChecker(final Long resourceId, final Long makerCheckerId) {
        return new EntityIdentifier(resourceId, makerCheckerId, null);
    }

    public static EntityIdentifier withChanges(final Long resourceId, final Map<String, Object> changes) {
        return new EntityIdentifier(resourceId, null, changes);
    }
    
    public static EntityIdentifier empty() {
        return new EntityIdentifier(Long.valueOf(-1), null, null);
    }

    public EntityIdentifier() {
        //
    }

    public EntityIdentifier(final Long entityId) {
        this.entityId = entityId;
        this.changes = new HashMap<String, Object>();
    }

    private EntityIdentifier(final Long entityId, final Long makerCheckerId, final Map<String, Object> changesOnly) {
        this.entityId = entityId;
        this.makerCheckerId = makerCheckerId;
        this.changes = changesOnly;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(final Long entityId) {
        this.entityId = entityId;
    }

    public Map<String, Object> getChanges() {
        Map<String, Object> checkIfEmpty = null;
        if (this.changes != null && !this.changes.isEmpty()) {
            checkIfEmpty = this.changes;
        }
        return checkIfEmpty;
    }
}
