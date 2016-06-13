# After migration all delegations should be exported again, set last delegation time to zero
UPDATE system_variable SET value=0 WHERE id=1;
