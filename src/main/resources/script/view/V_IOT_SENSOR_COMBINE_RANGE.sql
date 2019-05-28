CREATE
or replace
view v_iot_sensor_combine_range as select
	a.*,
	b.normal_value,
	b.sensor_code,
	c.mac_name,
	c.line_token,
	d.device_code,
	d.device_name,
	d.ou_code,
	e.alert_type,
	e.repeat_alert,
	e.repeat_unit,
	e.alert_message,
	e.is_active as is_active_combine
from
	iot_sensor_combine_detail a
join iot_sensor b on
	a.iot_sensor = b.id
join iot_machine c on
	b.iot_machine = c.id
join iot_device d on
	c.iot_device_id = d.id
join iot_sensor_combine e on
	a.iot_sensor_combine = e.id;