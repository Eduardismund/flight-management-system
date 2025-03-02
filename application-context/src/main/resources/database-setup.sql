create DATABASE flight_mgmt
use flight_mgmt
CREATE USER flight_mgmt FOR LOGIN flight_mgmt
go
exec sp_addrolemember 'db_owner', 'flight_mgmt'