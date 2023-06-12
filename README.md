# mine-spawners
A Minecraft 1.20 mod made with fabric that lets you mine spawners.
You can mine them with a silk touck pickaxe to get the spawner, doing so also makes the spawner not drop xp (for obvious reasons).

The mod reads a loot table from the json file at initialization, so spawners drop a spawner with the corresponding nbt data if mined with a silk touch pickaxe.
There's a mixin to allow players to put the spawner without resetting its contents (this happened when placed on survival mode).
And there's a second mixin, to stop giving xp when broken with a silk touch pick.
