-- 3) Trigger 1: soft delete Director + audit
CREATE OR ALTER TRIGGER trg_Director_SoftDelete
ON Director
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Soft delete
    UPDATE d
    SET hidden = 1
    FROM Director d
    INNER JOIN deleted del ON d.directorId = del.directorId;

    -- Audit (SEM details)
    INSERT INTO AuditLog (tableName, actionType, recordId, actionDate)
    SELECT
        'Director',
        'DELETE',
        del.directorId,
        GETDATE()
    FROM deleted del;
END;
GO

-- 4) Trigger 2: insert Actor + audit
CREATE OR ALTER TRIGGER trg_Actor_InsertAudit
ON Actor
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO AuditLog (tableName, actionType, recordId, actionDate)
    SELECT
        'Actor',
        'INSERT',
        i.actorId,
        GETDATE()
    FROM inserted i;
END;
GO
