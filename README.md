# 💀 Unstable Ban — Minecraft Plugin

**Unstable Ban** brings **true hardcore vibes** to your Minecraft server by turning **player deaths into bans**.
Every life counts — **one mistake and you're out**.

It functions just like the **banning system on Unstable SMP**, with a **few additional features**, but it's **fully configurable** to behave *exactly* like **Unstable SMP** if desired.

---

## ⚡ Core Idea

Death is no longer just a setback.

* You don't respawn
* You don't keep playing
* You get **banned**

Whether temporary or permanent — **death finally means something**.

---

## 💬 What Happens When a Player Dies

* The plugin **detects the death instantly**
* A **ban command is sent to LibertyBans**
* The player is **automatically banned**
* A **Wither spawn sound** plays
* Nearby players **see the death message and hear the sound** (configurable radius)
* The player sees a **custom ban reason** (killer or natural cause)

```yaml
# The radius in which the death message/sound will be seen/heard when someone dies. 1 = 1 Block
death-radius: 1000

# Should the plugin send a death message in the chat and/or play a sound when someone dies ONLY IN THE ASSIGNED RADIUS?
death-message: true
death-sound: true

# Only use sounds that are listed in https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html
death-sound-type: ENTITY_WITHER_SPAWN

# The reason the player should see if he dies
# %player% = the name of the player who died
# %killer% = the name of the player who killed the player (if there is one)
# %banCount% = the amount of bans the player has after being killed
ban-reason: "You were killed by %killer%!"

# when there is no killer, the player should see this message
ban-reason-natural-causes: "You died!"
```

No delays. No loopholes.
Just **pure consequences**.

---

## ⏳ Ban Progression System

Unstable Ban supports **escalating ban durations**, just like on **Unstable SMP**.

```yaml
# m = minutes, h = hours, d = days, mo = months
# for permanent bans, delete all durations except the first one and set it to "perm"
ban-durations:
  - "10m"
  - "1h"
  - "6h"
  - "24h"
  - "7d"

# Should a players bans be reset after the last/longest ban time
ban-durations-last-reset: true
```

Each death increases the punishment.
After reaching the longest duration, bans can automatically reset (optional).

Survive longer — or **pay the price**.

---

## 🔄 Earn Your Way Back In

If you're using **temporary bans**, players can **reduce their ban counter** in two ways.

### ⏱️ By Spending Time on the Server

```yaml
# Should someone lose one ban after a certain time period?
lose-ban-after-time: true

# s = seconds, m = minutes, h = hours, d = days
lose-ban-after-duration: "2h"

# use -1 to remove all bans after the time period
lose-ban-amount: 1
```

If a player is on ban level 2 and the time passes,
they'll drop back to ban level 1 — meaning **shorter bans next time**.

When they join while this system is active, a **boss bar countdown** shows the remaining time.

```yaml
# the color of the countdown boss bar
countdown-bossbar-color: "RED"

# The title on top of the boss bar
# Use %time% to show the remaining time until the next ban is removed
countdown-bossbar-title: "§cYou will lose one ban in %time%"
```

---

### ⚔️ By Killing Other Players

High risk. High reward.

```yaml
# Should someone lose ban(s) after getting a kill?
lose-ban-after-kill: true

# use -1 to remove all bans after the time period
lose-ban-amount-on-kill: -1
```

One kill can **wipe all previous bans** — if you're brave enough.

---

## 🧠 Why Unstable Ban Is Different

* **Death = Consequence**
* **Escalating punishments**
* **Redemption through skill or patience**
* **Public deaths with sound & messages**
* **Full logging & moderation transparency**
* **Fully configurable hardcore experience**

It's not just a plugin — it's **a ruleset for tension**.

---

### 📢 Join & Leave Radius Control

Join and leave messages can also be limited to a specific radius.

```yaml
# Should leave messages only be sent to players in a certain radius around the player who left?
leave-msg-radius-enabled: true
leave-msg-radius: 1000

# Should join messages only be sent to players in a certain radius around the player who joined?
join-msg-radius-enabled: true
join-msg-radius: 1000
```

---

## 🎮 Perfect For

* Hardcore SMPs
* Competitive survival servers
* Events & challenges
* Content creators
* Servers that want **real stakes**

Every fight matters.
Every heart matters.
Every decision matters.

---

## 📋 Commands — Unstable Ban

| Command                                 | Function                          | Permission            |
| --------------------------------------- | --------------------------------- | --------------------- |
| `/unstableban bans`                     | Check your current ban count      | Player                |
| `/ub bans`                              | Alias for `/unstableban bans`     | Player                |
| `/unstableban togglebossbar`            | Toggle your ban countdown bossbar | Player                |
| `/ub togglebossbar`                     | Alias                             | Player                |
| `/unstableban help`                     | Show help message                 | `unstableban.help`    |
| `/ub help`                              | Alias                             | `unstableban.help`    |
| `/unstableban reload`                   | Reload configuration              | `unstableban.reload`  |
| `/ub reload`                            | Alias                             | `unstableban.reload`  |
| `/unstableban getbans <player>`         | Get ban count of a player         | `unstableban.getbans` |
| `/unstableban setbans <player> <value>` | Set ban count of a player         | `unstableban.setbans` |

---

## 📦 Installation

1. Place `unstable-ban` in your `plugins/` folder
2. Make sure **LibertyBans** is installed
3. Restart your server
4. Deaths now trigger bans automatically

---

## 🔗 Dependencies

* [LibertyBans](https://libertybans.org/)

---

**Death finally means something.**
**Every life is precious. Every fight counts.**

Unstable Ban turns your server into a **true hardcore adventure** 💀🔥
