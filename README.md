# 💀 Unstable Ban — Minecraft Plugin

**Unstable Ban** brings **true hardcore vibes** to your Minecraft server by turning **player deaths into bans**.  
Every life counts — **one mistake and you’re out**.

It functions just like the **banning system on Unstable SMP**, with a **few additional features**, but it’s **fully configurable** to behave *exactly* like **Unstable SMP** if desired.

---

## ⚡ Core Idea

Death is no longer just a setback.

- You don’t respawn  
- You don’t keep playing  
- You get **banned**

Whether temporary or permanent — **death finally means something**.

---

## 💬 What Happens When a Player Dies

- The plugin **detects the death instantly**
- A **ban command is sent to your ban system**
- The player is **automatically banned**
- A **Wither spawn sound** plays
- Nearby players **see the death message and hear the sound**

```
# The radius in which the death message/sound will be seen/heard when someone dies. 1 = 1 Block
death-radius: 100
```

No delays. No loopholes.  
Just **pure consequences**.

---

## ⏳ Ban Progression System

Unstable Ban supports **escalating ban durations**, just like on **Unstable SMP**.

```
# m = minutes, h = hours, d = days, mo = months
# for permanent bans, delete all durations, except the first one, and set it to "perm"
ban-durations:
  - "10m"
  - "1h"
  - "6h"
  - "24h"
  - "7d"
```

Each death increases the punishment.  
Survive longer — or **pay the price**.

---

## 🔄 Earn Your Way Back In

If you’re using **temporary bans**, players can **reduce their ban counter** in two ways.

### ⏱️ By Spending Time on the Server

```
# Should someone lose one ban after a certain time period?
lose-ban-after-time: true

# s = seconds, m = minutes, h = hours, d = days
lose-ban-after-duration: "2h"

# use -1 to remove all bans after the time period
lose-ban-amount: 1
```

If a player is on ban level 2 and the time passes,  
they’ll drop back to ban level 1 — meaning **shorter bans next time**.

When they join while this system is active, a **boss bar countdown** shows the remaining time.

```
# the color of the countdown boss bar
countdown-bossbar-color: "RED"
```

---

### ⚔️ By Killing Other Players

High risk. High reward.

```
# Should someone lose ban(s) after getting a kill?
lose-ban-after-kill: true

# use -1 to remove all bans after the time period
lose-ban-amount-on-kill: -1
```

One kill can **wipe all previous bans** — if you’re brave enough.

---

## 🧠 Why Unstable Ban Is Different

- **Death = Consequence**
- **Escalating punishments**
- **Redemption through skill or patience**
- **Public deaths with sound & messages**
- **Fully configurable hardcore experience**

It’s not just a plugin — it’s **a ruleset for tension**.

---

## 🎮 Perfect For

- Hardcore SMPs  
- Competitive survival servers  
- Events & challenges  
- Content creators  
- Servers that want **real stakes**

Every fight matters.  
Every heart matters.  
Every decision matters.

---

# 📋 Commands — Unstable Ban

| Command                       | Function                                                                 | Permission                     |
|-------------------------------|--------------------------------------------------------------------------|-------------------------------|
| `/unstableban bans`            | Check your own current ban count                                         | player only           |
| `/ub bans`                     | Alias for `/unstableban bans`                                            | player only            |
| `/unstableban togglebossbar`   | Toggle visibility of your ban countdown bossbar if you have bans         | player only            |
| `/ub togglebossbar`            | Alias for `/unstableban togglebossbar`                                   | player only            |
| `/unstableban help`            | Show the help message with all available commands                        | `unstableban.help`            |
| `/ub help`                     | Alias for `/unstableban help`                                            | `unstableban.help`            |
| `/unstableban reload`          | Reload the plugin configuration                                          | `unstableban.reload`          |
| `/ub reload`                   | Alias for `/unstableban reload`                                          | `unstableban.reload`          |
| `/unstableban getbans <player>`| Get the current ban count of a specific player                            | `unstableban.getbans`         |
| `/unstableban setbans <player> <value>` | Set the ban count of a specific player                           | `unstableban.setbans`         |

---

## 📦 Installation

1. Place `unstable-ban` in your `plugins/` folder  
2. Make sure **LibertyBans** is installed  
3. Restart your server  
4. Deaths now trigger bans automatically  

---

## 🔗 Dependencies

- LibertyBans (https://libertybans.org/)

---

**Death finally means something.**  
**Every life is precious. Every fight counts.**  

Unstable Ban turns your server into a **true hardcore adventure** 💀🔥
