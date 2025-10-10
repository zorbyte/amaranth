# Amaranth

A bot made using JDA & Spring Boot (and quite a bit more in the future I hope).
I don't have much Java experience, but I tend to figure things out c:

## Features

- [ ] A verifications flow for gated communities.
- [ ] A tickets system.
- [ ] More to come (secrets c:)

## Contributing

If you wish to contribute to the project, you should reach out to me on Discord @zorbyte.
We have a board with design mock-ups, design plans, and the ability to allocate tasks via kanban.

### Project organisation

Project organisation is heavily inspired by Domain Driven Design.

Modules has the following strucure:

```
dev.zorbyte.amaranth/
  [module]/
    commands/ - Triggers from our Application layer if you will.
                Logic that drives a user's interaction with us lives here.
                Commands are not public to other modules.
    internal/ - All business logic that deals with processing events dispatched
                that the domain needs to know about. No files in here are public
                to other modules, they're usually all services, configs, and components
                loaded by spring.
        [...] - Files in the root of a module are our domain modules,
                they describe events that happen in the bot (e.g. UserAddedToTicket)
                as well as the data structures behind them.
```

> You can think of `commands` as an application layer, `internal` as a hybrid of application and infrastructure (which would ordinarily be sub-packaged out further in the `internal` package), and the module files (`[...]`)as the domain.
