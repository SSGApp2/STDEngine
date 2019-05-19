CREATE
or replace
view v_iot_sensor_range as select
	a.*,
	b.mac_name,
	b.line_token,
	c.device_code,
	c.device_name,
	c.ou_code
from
	iot_sensor a
join iot_machine b on
	a.iot_machine = b.id
join iot_device c on
	b.iot_device_id = c.id;