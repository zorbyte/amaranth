# Amaranth

A bot made using JDA & Spring Boot (and quite a bit more in the future I hope).

## Features

- [ ] A verifications flow for gated communities.
- [ ] A tickets system.
- [ ] More to come (secrets c:)

## Contributing

If you wish to contribute to the project, you should reach out to me on Discord @zorbyte.
We have a board with design mock-ups, design plans, and the ability to allocate tasks via kanban.

### Development Setup

#### Prerequisites

- JDK 25 or later
- PostgreSQL (for production)
- Docker (for running PostgreSQL in development)

#### Building & Running

1. Clone the repository:

   ```bash
   git clone https://github.com/zorbyte/amaranth.git
   cd amaranth
   ```

2. Build the project:

   ```bash
   ./gradlew build
   ```

3. Run in development mode:

   ```bash
   ./gradlew bootRun
   ```

#### Configuraiton

The following environment variables are required:

- `BOT_TOKEN`: Your Discord bot token
- `TEST_GUILD_ID`: Guild ID for testing slash commands

You can also specify them in an applicaiton configuration if you are so inclined,
just make sure not to commit them!

### Code Style & Quality

We use several tools for code quality checks, including checkstyle,
spotless, and the eclipse formatter for use in Eclipse or with the VSCode
Java Plugin by RedHat (you will need to install a plugin for Intellij). You can run Checkstyle and/or Spotless by doing the following:

- **Checkstyle**: Enforces Java coding standards

  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```

- **Spotless**: Formats code automatically

  ```bash
  # Check if code needs formatting
  ./gradlew spotlessCheck

  # Apply formatting
  ./gradlew spotlessApply
  ```

Run all quality checks:

```bash
./gradlew check
```

### Project organisation

Project organisation has some themes from domain driven design,
but is not in any way faithful to it, as that is outside the scope
of this project for the time being.

Modules have the following strucure:

<!-- markdownlint-disable-next-line fenced-code-language -->
```
dev.zorbyte.amaranth/
  [module]/
    commands/ - Triggers from our Application layer if you will.
                Logic that drives a user's interaction with us lives here.
                Commands are not public to other modules.
    internal/ - All business logic that deals with processing events dispatched
                that the "domain" needs to know about. No files in here are public
                to other modules, they're usually all services, configs, and components
                loaded by spring.
        [...] - Files in the root of a module are our "domain" modules,
                they describe events that happen in the bot (e.g. UserAddedToTicket)
                as well as the data structures behind them.
```

> You can think of `commands` as an application layer, `internal` as a hybrid of application and infrastructure (which would ordinarily be sub-packaged out further in the `internal` package), and the module files (`[...]`) as the domain.

## License

We're licensed under the [GNU AGPL 3 license](./LICENSE).
