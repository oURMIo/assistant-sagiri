# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Sagiri is a personal Telegram assistant bot built with Spring Boot 3.5 and Java 21.
It checks the status of self-hosted server clusters and surfaces small utilities
(holidays, useful URLs, project lists) through Telegram commands and inline buttons.

The Gradle project root is `app/` — **all build/test commands run from there**, not the
repository root.

## Commands

```bash
cd app

./gradlew build           # compile + test + assemble
./gradlew test            # run all tests (finalizedBy jacocoTestReport)
./gradlew bootRun         # run the bot locally
./gradlew bootJar         # produce runnable jar in build/libs/

# Run a single test class or method
./gradlew test --tests com.dach.sagiri.service.WebServiceTest
./gradlew test --tests com.dach.sagiri.service.WebServiceTest.methodName
```

JaCoCo coverage report (XML/CSV/HTML) is generated under `app/build/` after `test`;
HTML lands in `app/build/jacocoHtml/`. CI (`.github/workflows/gradle-test.yml`) runs
`./gradlew clean test` on every push and PR.

Docker: `docker-compose up --build` builds the image from `app/Dockerfile` (multi-stage
Gradle build → JDK 21 runtime) and runs the bot as `assistant-sagiri`.

## Architecture

The bot uses a **registry-of-handlers** pattern driven by Spring dependency injection.
Adding a feature means adding a `@Component` that implements an interface — Spring auto-wires
it into the dispatcher; no central switch statement needs editing.

**Startup & dispatch flow:**
1. `BotConfig` creates the `TelegramBot` bean from `bot.key`.
2. `BotInitializer` (`@PostConstruct`) registers `BotMessageService` as the updates listener.
3. `BotMessageService.registerListener` polls Telegram updates. For each update it runs
   `processCallback` (inline-button presses) then `processMessage` (text commands).
4. Dispatch is keyed on the first whitespace-separated token of the message/callback text:
   - **Commands** → `CommandService` looks up `BotCommand.command()` (e.g. `/start`).
   - **Callbacks** → `CallbackService` looks up `BotCallback.callback()` (e.g. `cluster_status`).
   - Unmatched input replies with a "select a function from /help" message.

**Two handler interfaces** (`com.dach.sagiri.bot`):
- `BotCommand` — `command()` returns the trigger string (`/...`); `execute(bot, Message)`.
- `BotCallback` — `callback()` returns the inline-button data key; `execute(bot, CallbackQuery)`.

`CommandService` / `CallbackService` each receive a `List<...>` of all handler beans via
constructor injection and build a `Map<key, handler>`. **The map key comes from the handler's
own `command()`/`callback()` method**, so a new handler is registered simply by being a
`@Component`.

**Services** (`com.dach.sagiri.service`):
- `WebService` — all outbound HTTP (Java `HttpClient`) and the raw-socket cluster reachability
  check (TCP connect to SSH port 22). `doSafeRequest` sends browser-like headers for scraping;
  `doRequestForList` deserializes JSON arrays via Jackson.
- `HolidayService` — fetches holidays from the Nager.Date API and scrapes
  kakoysegodnyaprazdnik.ru with Jsoup.
- `FileService` / `FileProperty` — load bundled JSON resources (`useful-url.json`,
  `project-list.json`) from the classpath into DTOs.

**Configuration** (`com.dach.sagiri.property`):
- Secrets/config live in `app/src/main/resources/config.properties` (bot key, cluster domains),
  imported into Spring via `application.yaml`'s `spring.config.import`.
- `@Value`-backed beans (`BotConfigProperty`, `ClusterProperty`) expose those properties.
  Note `config.properties` currently contains a committed bot token and cluster domains.

## Conventions

- Constructor injection throughout (no field injection).
- Handlers stay thin: parse input, call a service, send a `SendMessage`. Business/IO logic
  belongs in a service.
- Outbound HTTP and socket checks go through `WebService` — don't create new `HttpClient`s
  in handlers or other services.
- SLF4J logging with `LoggerFactory.getLogger(...)`; failures are logged and swallowed into
  `Optional.empty()` / empty lists rather than propagated, so the bot's update loop never dies.