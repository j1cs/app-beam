include .env
export

.PHONY: srv
srv:
	@echo "Running only services containers"
	@docker compose -f docker-compose.srv.yml up -d --remove-orphans

.PHONY: build
build:
	@echo "Building containers"
	@docker compose -f docker-compose.run.yml build

dev: srv
	@echo "Starting development environment"
	@./scripts/dev.sh

.PHONY: stop
stop:
	@echo "Stopping local containers"
	@docker compose -f docker-compose.srv.yml stop

.PHONY: down
down:
	@echo "Stopping and removing local containers"
	@docker compose -f docker-compose.srv.yml down