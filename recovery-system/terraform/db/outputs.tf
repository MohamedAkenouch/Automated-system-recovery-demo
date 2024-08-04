output "container_name" {
  value = docker_container.database.name
}

output "volume_name" {
  value = docker_volume.db_volume.name
}

output "db_id_address" {
  value = docker_container.database.network_data[0].ip_address
}