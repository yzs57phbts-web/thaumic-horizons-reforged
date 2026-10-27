package thaumichorizons;
import java.awt.Color;
import java.util.*;
import thaumcraft.aspect.*;
import thaumichorizons.legacy.NoiseGeneratorOctaves;
import thaumichorizons.PocketTerrain.Plan;
import static thaumichorizons.TerrainWorld.*;

/** Original terrain algorithms from the pinned upstream; see audit/pocket-generation.json. */
final class PocketGenerator {
    private int creatures;
    private static final int MAX_CREATURES=100;
    private static int fastFloor(Number a){return a.intValue()>a.floatValue()?a.intValue()-1:a.intValue();}
    private static void setBlock(TerrainWorld world,int x,int y,int z,TerrainBlock block,int metadata){world.setBlock(x,y,z,block,metadata);}
    static Plan generate(AspectList aspects,long seed,int id){
        var generator=new PocketGenerator();int radius=aspects.visSize()*.75F<128?(int)Math.max(32,aspects.visSize()*.75F):127;
        var data=new Plan(id,radius,seed);var world=new TerrainWorld(data,seed);data.color=generator.getColor(aspects);var bio=generator.setBiome(0,128,id*256,data,world,aspects);int noise=fastFloor(generator.calcNoise(aspects)),life=fastFloor(generator.calcLife(aspects));
        generator.drawLayers(0,128,id*256,data,world,aspects,noise,bio,life);generator.drawCaves(0,128,id*256,data,world,aspects,noise);generator.drawPockets(0,128,id*256,data,world,aspects,noise);generator.addEffects(data,aspects);generator.drawRings(0,128,id*256,data,world,aspects);generator.drawSphere(0,128,id*256,radius,TerrainBlocks.blockVoid,0,world);
        for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++){world.setBlock(x,128,id*256+z,TerrainBlocks.blockCosmeticSolid,6);world.setBlock(x,129,id*256+z,TerrainBlocks.air,0);world.setBlock(x,130,id*256+z,TerrainBlocks.air,0);}return data;
    }
    int getColor(final AspectList aspects) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (final Aspect asp : aspects.getAspects()) {
            if (asp != null) {
                final Color aspColor = new Color(asp.getColor());
                r += (int) (aspColor.getRed() * aspectFraction(asp, aspects));
                g += (int) (aspColor.getGreen() * aspectFraction(asp, aspects));
                b += (int) (aspColor.getBlue() * aspectFraction(asp, aspects));
            }
        }
        return r * 256 * 256 + g * 256 + b;
    }

    public void addEffects(final Plan data, final AspectList aspects) {
        int pointer = 0;
        data.potionEffects = new int[8];
        if (aspects.getAmount(Aspect.MOTION) > 0) {
            data.potionEffects[pointer] = 1;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.FLIGHT) > 0) {
            data.potionEffects[pointer] = 8;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.HEAL) > 0) {
            data.potionEffects[pointer] = 10;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.TRAVEL) > 0) {
            data.potionEffects[pointer] = 1;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.TOOL) > 0) {
            data.potionEffects[pointer] = 3;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.WEAPON) > 0) {
            data.potionEffects[pointer] = 5;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            data.potionEffects[pointer] = 11;
            ++pointer;
        }
    }

    public boolean drawRings(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects) {
        boolean drewAnything = false;
        int numRings = 0;
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, TerrainBlocks.blockCosmeticSolid, 11, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.DARKNESS) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, TerrainBlocks.obsidian, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, TerrainBlocks.blockCosmeticSolid, 11, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CRYSTAL) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, TerrainBlocks.glass, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.COLD) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, TerrainBlocks.packed_ice, 0, world);
            drewAnything = true;
        }
        return drewAnything;
    }

    public double calcNoise(final AspectList aspects) {
        double noise = 50d;
        noise -= (aspectFraction(Aspect.ORDER, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.ENTROPY, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.MOTION, aspects) * 50.0d);
        noise += (aspectFraction(Aspect.EXCHANGE, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.FLIGHT, aspects) * 50.0d);
        noise -= (aspectFraction(Aspect.TRAVEL, aspects) * 100.0d);
        if (noise > 100d) {
            return 100d;
        }
        return Math.max(noise, 0d);
    }

    public double calcLife(final AspectList aspects) {
        double life = 0;
        life += (aspectFraction(Aspect.LIGHT, aspects) * 25.0d);
        life += (aspectFraction(Aspect.WEATHER, aspects) * 25.0d);
        life += (aspectFraction(Aspect.LIFE, aspects) * 150.0d);
        life -= (aspectFraction(Aspect.POISON, aspects) * 75.0d);
        life -= (aspectFraction(Aspect.DEATH, aspects) * 100.0d);
        life -= (aspectFraction(Aspect.DARKNESS, aspects) * 25.0d);
        life += (aspectFraction(Aspect.SOUL, aspects) * 50.0d);
        life += (aspectFraction(Aspect.HEAL, aspects) * 150.0d);
        life += (aspectFraction(Aspect.AURA, aspects) * 50.0d);
        life += (aspectFraction(Aspect.SLIME, aspects) * 50.0d);
        life += (aspectFraction(Aspect.PLANT, aspects) * 100.0d);
        life += (aspectFraction(Aspect.TREE, aspects) * 100.0d);
        life += (aspectFraction(Aspect.BEAST, aspects) * 100.0d);
        life += (aspectFraction(Aspect.FLESH, aspects) * 50.0d);
        life -= (aspectFraction(Aspect.UNDEAD, aspects) * 50.0d);
        life += (aspectFraction(Aspect.MIND, aspects) * 25.0d);
        life += (aspectFraction(Aspect.SENSES, aspects) * 25.0d);
        life += (aspectFraction(Aspect.MAN, aspects) * 25.0d);
        life += (aspectFraction(Aspect.CROP, aspects) * 50.0d);
        life += (aspectFraction(Aspect.HARVEST, aspects) * 25.0d);
        life -= (aspectFraction(Aspect.WEAPON, aspects) * 25.0d);
        life -= (aspectFraction(Aspect.HUNGER, aspects) * 25.0d);
        life += (aspectFraction(Aspect.CLOTH, aspects) * 25.0d);
        if (life < 0d) {
            life = 0d;
        } else if (life > 100d) {
            life = 100d;
        }
        return life;
    }

    public double aspectFraction(final Aspect asp, final AspectList aspects) {
        return (double) aspects.getAmount(asp) / (double) aspects.visSize();
    }

    private Aspect getMainAspect(final AspectList aspects) {
        int max = 0;
        Aspect ret = null;
        for (Aspect a : aspects.getAspects()) {
            if (aspects.getAmount(a) > max) {
                max = aspects.getAmount(a);
                ret = a;
            }
        }
        return ret;
    }

    public void drawLayers(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise, final TerrainBiome bio, final int life) {
        final int total = aspects.visSize();
        int level = yCenter;
        boolean drewAnything = false;

        if (aspects.getAmount(Aspect.COLD) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.packed_ice, 0, level, 0, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.COLD, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.water, 0, level, 0, bio, life, aspects);
            level -= fastFloor((aspectFraction(Aspect.WATER, aspects) * data.radius / 1.5f));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    TerrainBlocks.blockTaint,
                    1,
                    level,
                    noise,
                    bio,
                    life,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.TAINT, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CLOTH) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.wool, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.CLOTH, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FLESH) > 0) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    TerrainBlocks.blockTaint,
                    2,
                    level,
                    noise,
                    null,
                    0,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.CLOTH, aspects) * data.radius));
            drewAnything = true;
        }

        if (aspects.getAmount(Aspect.EARTH) > 0) {
            if (bio == TerrainBiome.desert) {
                drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.sand, 0, level, noise, bio, life, aspects);
            } else {
                drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.dirt, 0, level, noise, bio, life, aspects);
            }
            if (aspects.getAmount(Aspect.ORDER) >= aspects.getAmount(Aspect.ENTROPY)) {
                drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.stone, 0, level - 5, noise, null, 0, aspects);
            } else {
                drawLayer(
                        xCenter,
                        yCenter,
                        zCenter,
                        data,
                        world,
                        TerrainBlocks.cobblestone,
                        0,
                        level - 5,
                        noise,
                        null,
                        0,
                        aspects);
            }
            level -= fastFloor((aspectFraction(Aspect.EARTH, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.netherrack, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.FIRE, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, TerrainBlocks.obsidian, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.ARMOR, aspects) * data.radius));
            drewAnything = true;
        }
        if (!drewAnything) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    TerrainBlocks.blockDust,
                    0,
                    level,
                    noise,
                    bio,
                    life,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.ARMOR, aspects) * data.radius));
            drewAnything = true;
        }
    }

    public void drawLayer(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final TerrainBlock block, final int md, final int level, final int noise,
            final TerrainBiome bio, final int life, final AspectList aspects) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseData = null;
        if (noise != 0) {
            noiseData = noiseGen.generateNoiseOctaves(
                    noiseData,
                    xCenter - data.radius,
                    yCenter,
                    zCenter - data.radius,
                    2 * data.radius,
                    1,
                    2 * data.radius,
                    noise / 25.0f,
                    noise / 50.0f,
                    noise / 25.0f);
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                int top = 0;
                if (noise != 0
                        && x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2) < noiseData.length) {
                    top = (int) (noiseData[x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2)]
                            / 8.0);
                }
                final int offsettop = -level + yCenter;
                final int ctop = x * x + z * z + offsettop * offsettop - data.radius * data.radius;
                final int tmax = offsettop + (int) Math.sqrt(offsettop * offsettop - ctop);
                final int offsetbottom = level - yCenter;
                final int cbottom = x * x + z * z + offsetbottom * offsetbottom - data.radius * data.radius;
                final int bmax = offsetbottom + (int) Math.sqrt(offsetbottom * offsetbottom - cbottom);
                if (top > tmax) {
                    top = tmax;
                }
                int y;
                for (int bottom = y = -bmax; y <= top; ++y) {
                    if (top != bottom && level + y > 0) {
                        if (y == top) {
                            if (block != null) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, block, md);
                            }
                            if (block == TerrainBlocks.water) {
                                if (bio != null && bio.getTempCategory() == TerrainBiome.TempCategory.COLD) {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, TerrainBlocks.ice, 0);
                                } else {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                    if ((life > 40 || aspects.getAmount(Aspect.BEAST) > 0)
                                            && world.rand.nextInt(100) > 98
                                            && creatures < MAX_CREATURES) {
                                        final TerrainMob squiddie = new TerrainMob("minecraft:squid");
                                        squiddie.setPosition(x + xCenter, y + level, z + zCenter);
                                        squiddie.func_110163_bv();
                                        world.spawnEntityInWorld(squiddie);
                                        ++creatures;
                                    }
                                    if (life > 0 && world.rand.nextInt(100) > 98) {
                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, TerrainBlocks.waterlily, 0);
                                    }
                                }
                            } else if (block == TerrainBlocks.dirt) {
                                if (life > 0 && world.isAirBlock(x + xCenter, y + level + 1, z + zCenter)) {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, TerrainBlocks.grass, 0);
                                    if (life >= 10) {
                                        if ((life >= 20 || aspects.getAmount(Aspect.CROP) > 0
                                                || aspects.getAmount(Aspect.HARVEST) > 0)
                                                && world.rand.nextInt(100) > 97) {
                                            switch (world.rand.nextInt(10)) {
                                                case 0 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            TerrainBlocks.farmland,
                                                            0);
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.carrots,
                                                            0);
                                                }
                                                case 1 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            TerrainBlocks.farmland,
                                                            0);
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.potatoes,
                                                            0);
                                                }
                                                case 2 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            TerrainBlocks.farmland,
                                                            0);
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.wheat,
                                                            0);
                                                }
                                                case 3 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.melon_block,
                                                            0);
                                                }
                                                case 4 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.pumpkin,
                                                            0);
                                                }
                                                case 5 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.reeds,
                                                            0);
                                                }
                                                case 6 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            TerrainBlocks.mycelium,
                                                            0);
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.red_mushroom,
                                                            0);
                                                }
                                                case 7 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            TerrainBlocks.mycelium,
                                                            0);
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.brown_mushroom,
                                                            0);
                                                }
                                                case 8 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.blockCustomPlant,
                                                            2);
                                                }
                                                case 9 -> {
                                                    setBlock(
                                                            world,
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            TerrainBlocks.blockCustomPlant,
                                                            5);
                                                }
                                            }
                                        } else if ((life >= 15 || aspects.getAmount(Aspect.TREE) > 0)
                                                && world.rand.nextInt(100) > 97) {
                                                    switch (world.rand.nextInt(10)) {
                                                        case 0 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    0);
                                                        }
                                                        case 1 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    1);
                                                        }
                                                        case 2 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    2);
                                                        }
                                                        case 3 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    3);
                                                        }
                                                        case 4 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    4);
                                                        }
                                                        case 5 -> {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.sapling,
                                                                    5);
                                                        }
                                                        case 6 -> {
                                                            if (((bio != null
                                                                    && bio.biomeID == TerrainBiome.magicalForest.biomeID)
                                                                    || aspects.getAmount(Aspect.MAGIC) > 10
                                                                    || aspects.getAmount(Aspect.AURA) > 5)
                                                                    && world.rand.nextInt(4) == 0) {
                                                                setBlock(
                                                                        world,
                                                                        x + xCenter,
                                                                        y + level + 1,
                                                                        z + zCenter,
                                                                        TerrainBlocks.blockCustomPlant,
                                                                        1);
                                                                break;
                                                            }
                                                        }
                                                        case 7 -> {
                                                            if (world.rand.nextInt(3) == 0) {
                                                                setBlock(
                                                                        world,
                                                                        x + xCenter,
                                                                        y + level + 1,
                                                                        z + zCenter,
                                                                        TerrainBlocks.blockCustomPlant,
                                                                        0);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else
                                            if ((life >= 10 || aspects.getAmount(Aspect.PLANT) > 0)
                                                    && world.rand.nextInt(100) > 94) {
                                                        final int random = world.rand.nextInt(18);
                                                        if (random <= 15) {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.red_flower,
                                                                    random);
                                                        } else {
                                                            setBlock(
                                                                    world,
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    TerrainBlocks.tallgrass,
                                                                    random - 16);
                                                        }
                                                    }
                                    }
                                } else {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                }
                            } else if (block == TerrainBlocks.sand) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                if (life > 10) {
                                    if (life >= 20 && world.rand.nextInt(100) > 98) {
                                        setBlock(
                                                world,
                                                x + xCenter,
                                                y + level + 1,
                                                z + zCenter,
                                                TerrainBlocks.blockCustomPlant,
                                                3);
                                    } else if (world.rand.nextInt(100) > 97) {
                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, TerrainBlocks.cactus, 0);
                                    }
                                }
                            } else if (block == TerrainBlocks.blockTaint && md == 2 && world.rand.nextInt(300) > 298) {
                                if (world.rand.nextBoolean()) {
                                    for (int ecks = 0; ecks < 3; ++ecks) {
                                        for (int zee = 0; zee < 2; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                setBlock(
                                                        world,
                                                        x + xCenter + ecks,
                                                        y + level + why,
                                                        z + zCenter + zee,
                                                        TerrainBlocks.blockBrain,
                                                        0);
                                            }
                                        }
                                    }
                                } else {
                                    for (int ecks = 0; ecks < 2; ++ecks) {
                                        for (int zee = 0; zee < 3; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                setBlock(
                                                        world,
                                                        x + xCenter + ecks,
                                                        y + level + why,
                                                        z + zCenter + zee,
                                                        TerrainBlocks.blockBrain,
                                                        0);
                                            }
                                        }
                                    }
                                }
                            }
                            if (bio != null && block.isNormalCube()
                                    && bio.getTempCategory() == TerrainBiome.TempCategory.COLD) {
                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, TerrainBlocks.snow_layer, 1);
                            }
                            if ((life >= 50 || aspects.getAmount(Aspect.BEAST) > 0) && world.rand.nextInt(100) > 98) {
                                TerrainMob critter = null;
                                switch (world.rand.nextInt(7)) {
                                    case 0 -> {
                                        critter = new TerrainMob("minecraft:cow");
                                    }
                                    case 1 -> {
                                        critter = new TerrainMob("minecraft:pig");
                                    }
                                    case 2 -> {
                                        critter = new TerrainMob("minecraft:chicken");
                                    }
                                    case 3 -> {
                                        critter = new TerrainMob("minecraft:sheep");
                                    }
                                    case 4 -> {
                                        critter = new TerrainMob("minecraft:horse");
                                    }
                                    case 5 -> {
                                        critter = new TerrainMob("minecraft:ocelot");
                                    }
                                    case 6 -> {
                                        critter = new TerrainMob("minecraft:wolf");
                                    }
                                }
                                if (critter != null && creatures < MAX_CREATURES) {
                                    critter.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                    world.spawnEntityInWorld(critter);
                                    ++creatures;
                                }
                            }
                            if ((aspects.getAmount(Aspect.POISON) > 0 || aspects.getAmount(Aspect.EXCHANGE) > 0
                                    || aspects.getAmount(Aspect.METAL) > 0
                                    || aspects.getAmount(Aspect.MECHANISM) > 0) && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final TerrainMob slime = new TerrainMob("thaumichorizons:mercurial_slime");
                                slime.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime);
                                ++creatures;
                            }
                            if ((aspects.getAmount(Aspect.SLIME) > 0 || aspects.getAmount(Aspect.FLESH) > 0
                                    || aspects.getAmount(Aspect.HUNGER) > 0) && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final TerrainMob slime2 = new TerrainMob("thaumichorizons:meat_slime");
                                slime2.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime2);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.SLIME) > 0 && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final TerrainMob slime3 = new TerrainMob("minecraft:slime");
                                slime3.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime3);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.ELDRITCH) > 0 && world.rand.nextInt(200) > 198) {
                                setBlock(
                                        world,
                                        x + xCenter,
                                        y + level,
                                        z + zCenter,
                                        TerrainBlocks.blockCosmeticSolid,
                                        1);
                                setBlock(
                                        world,
                                        x + xCenter,
                                        y + level + 1,
                                        z + zCenter,
                                        TerrainBlocks.blockCosmeticSolid,
                                        0);
                                setBlock(
                                        world,
                                        x + xCenter,
                                        y + level + 2,
                                        z + zCenter,
                                        TerrainBlocks.blockCosmeticSolid,
                                        0);
                                setBlock(
                                        world,
                                        x + xCenter,
                                        y + level + 3,
                                        z + zCenter,
                                        TerrainBlocks.blockCosmeticSolid,
                                        0);
                            }
                            if (aspects.getAmount(Aspect.CRAFT) > 0 && world.rand.nextInt(200) > 198) {
                                setBlock(
                                        world,
                                        x + xCenter,
                                        y + level + 1,
                                        z + zCenter,
                                        TerrainBlocks.blockStoneDevice,
                                        1);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(100) > 98) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, TerrainBlocks.blockTaintFibres, 0);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(200) > 198
                                    && creatures < MAX_CREATURES) {
                                final TerrainMob slime4 = new TerrainMob("thaumcraft:taintacle");
                                slime4.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime4);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(200) > 198
                                    && creatures < MAX_CREATURES) {
                                final TerrainMob slime5 = new TerrainMob("thaumcraft:taint_spore_swarmer");
                                slime5.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime5);
                                ++creatures;
                            }
                        } else if (block != null) {
                            setBlock(world, x + xCenter, y + level, z + zCenter, block, md);
                        }
                    }
                }
            }
        }
    }

    public void drawCaves(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise) {
        float solidity = 0.5f;
        solidity -= aspectFraction(Aspect.AIR, aspects) * 0.5f;
        solidity += aspectFraction(Aspect.ORDER, aspects) * 0.25f;
        solidity -= aspectFraction(Aspect.VOID, aspects);
        solidity -= aspectFraction(Aspect.FLIGHT, aspects) * 0.5f;
        solidity += aspectFraction(Aspect.CRAFT, aspects) * 0.5f;
        solidity -= aspectFraction(Aspect.TRAP, aspects) * 0.5f;
        if (solidity <= 0.0f) {
            solidity = 0.0f;
        } else if (solidity > 1.0f) {
            solidity = 1.0f;
        }
        final int numCaves = (int) (data.radius * data.radius * data.radius / 10000 * (solidity + 0.1f));
        final LinkedHashMap<Aspect, Number> dressing = new LinkedHashMap<>();
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            dressing.put(Aspect.EARTH, aspectFraction(Aspect.EARTH, aspects));
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            dressing.put(Aspect.FIRE, aspectFraction(Aspect.FIRE, aspects));
        }
        if (aspects.getAmount(Aspect.ORDER) > 0) {
            dressing.put(Aspect.ORDER, aspectFraction(Aspect.ORDER, aspects));
        }
        if (aspects.getAmount(Aspect.AIR) > 0) {
            dressing.put(Aspect.AIR, aspectFraction(Aspect.AIR, aspects));
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            dressing.put(Aspect.WATER, aspectFraction(Aspect.WATER, aspects));
        }
        if (aspects.getAmount(Aspect.ENTROPY) > 0) {
            dressing.put(Aspect.ENTROPY, aspectFraction(Aspect.ENTROPY, aspects));
        }
        if (aspects.getAmount(Aspect.METAL) > 0) {
            dressing.put(Aspect.METAL, aspectFraction(Aspect.METAL, aspects));
        }
        if (aspects.getAmount(Aspect.DEATH) > 0) {
            dressing.put(Aspect.DEATH, aspectFraction(Aspect.DEATH, aspects));
        }
        if (aspects.getAmount(Aspect.UNDEAD) > 0) {
            dressing.put(Aspect.UNDEAD, aspectFraction(Aspect.UNDEAD, aspects));
        }
        if (aspects.getAmount(Aspect.SENSES) > 0) {
            dressing.put(Aspect.SENSES, aspectFraction(Aspect.SENSES, aspects));
        }
        if (aspects.getAmount(Aspect.GREED) > 0) {
            dressing.put(Aspect.GREED, aspectFraction(Aspect.GREED, aspects));
        }
        for (int i = 0; i < numCaves; ++i) {
            drawACave(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    aspects,
                    noise,
                    TerrainBlocks.air,
                    world.rand.nextInt(10) + 10,
                    world.rand.nextInt(10) + 10,
                    world.rand.nextInt((int) (data.radius * 1.5f)) - (int) (data.radius * 0.75f),
                    -world.rand.nextInt((int) (data.radius * 0.5f)),
                    world.rand.nextInt((int) (data.radius * 1.5f)) - (int) (data.radius * 0.75f),
                    dressing);
        }
    }

    public void drawACave(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise, final TerrainBlock block, final int xSize,
            final int zSize, final int xOffset, final int yOffset, final int zOffset,
            final LinkedHashMap<Aspect, Number> dressing) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseData = null;
        noiseData = noiseGen.generateNoiseOctaves(
                noiseData,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                16,
                zSize,
                noise / 10.0f,
                noise / 25.0f,
                noise / 10.0f);
        final int avg = getAvg(noiseData);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                for (int y = 0; y < 16; ++y) {
                    if (noiseData[x + xSize * z + y * xSize * zSize] > avg && world.getBlock(
                            xCenter - xSize + xOffset + x,
                            yCenter + yOffset + y,
                            zCenter - zSize + zOffset + z) != TerrainBlocks.water) {
                        if (!world.isAirBlock(
                                xCenter - xSize + xOffset + x,
                                yCenter + yOffset + y - 1,
                                zCenter - zSize + zOffset + z) && world.rand.nextInt(50) == 49) {
                            float which = world.rand.nextFloat();
                            Aspect whichAspect = null;
                            final Iterator<Aspect> it = dressing.keySet().iterator();
                            boolean doDressing = false;
                            while (it.hasNext()) {
                                whichAspect = it.next();
                                if (which <= dressing.get(whichAspect).floatValue()) {
                                    doDressing = true;
                                    break;
                                }
                                which -= dressing.get(whichAspect).floatValue();
                            }
                            if (doDressing && whichAspect != null
                                    && !world.isAirBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z)
                                    && world.getBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z) != TerrainBlocks.water) {
                                if (whichAspect == Aspect.EARTH) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            3);
                                } else if (whichAspect == Aspect.AIR) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            0);
                                } else if (whichAspect == Aspect.WATER) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            2);
                                } else if (whichAspect == Aspect.FIRE) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            1);
                                } else if (whichAspect == Aspect.ORDER) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            4);
                                } else if (whichAspect == Aspect.ENTROPY) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.blockCrystal,
                                            5);
                                } else if (whichAspect == Aspect.DEATH || whichAspect == Aspect.UNDEAD) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.skull,
                                            0);
                                } else if (whichAspect == Aspect.SENSES) {
                                    setBlock(
                                            world,
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            TerrainBlocks.lapis_block,
                                            0);
                                } else if (whichAspect == Aspect.GREED) {
                                    if (world.rand.nextBoolean()) {
                                        setBlock(
                                                world,
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                TerrainBlocks.gold_block,
                                                0);
                                    } else {
                                        setBlock(
                                                world,
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                TerrainBlocks.emerald_block,
                                                0);
                                    }
                                } else if (whichAspect == Aspect.METAL) {
                                    if (world.rand.nextBoolean()) {
                                        setBlock(
                                                world,
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                TerrainBlocks.gold_block,
                                                0);
                                    } else {
                                        setBlock(
                                                world,
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                TerrainBlocks.iron_block,
                                                0);
                                    }
                                }
                            }
                        } else {
                            setBlock(
                                    world,
                                    xCenter - xSize + xOffset + x,
                                    yCenter + yOffset + y,
                                    zCenter - zSize + zOffset + z,
                                    TerrainBlocks.air,
                                    0);
                        }
                    }
                }
            }
        }
    }

    public void drawRavines(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise) {}

    public void drawClouds(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise) {}

    public void drawSurfaceFeatures(final int xCenter, final int yCenter, final int zCenter,
            final Plan data, final TerrainWorld world, final AspectList aspects, final int noise,
            final float life) {}

    public void drawUndergroundFeatures(final int xCenter, final int yCenter, final int zCenter,
            final Plan data, final TerrainWorld world, final AspectList aspects, final int noise,
            final float life) {}

    public void drawLeviathanBones(final int xCenter, final int yCenter, final int zCenter,
            final Plan data, final TerrainWorld world, final AspectList aspects, final int noise) {}

    public int getAvg(final double[] array) {
        long x = 0L;
        for (double v : array) {
            x += (long) v;
        }
        x /= array.length;
        return (int) x;
    }

    public void drawPockets(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise) {
        final int numPockets = data.radius * data.radius * data.radius / 200;
        final LinkedHashMap<Aspect, Number> probabilities = new LinkedHashMap<>();
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            probabilities.put(Aspect.EARTH, aspectFraction(Aspect.EARTH, aspects));
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            probabilities.put(Aspect.FIRE, aspectFraction(Aspect.FIRE, aspects));
        }
        if (aspects.getAmount(Aspect.ORDER) > 0) {
            probabilities.put(Aspect.ORDER, aspectFraction(Aspect.ORDER, aspects));
        }
        if (aspects.getAmount(Aspect.AIR) > 0) {
            probabilities.put(Aspect.AIR, aspectFraction(Aspect.AIR, aspects));
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            probabilities.put(Aspect.WATER, aspectFraction(Aspect.WATER, aspects));
        }
        if (aspects.getAmount(Aspect.ENTROPY) > 0) {
            probabilities.put(Aspect.ENTROPY, aspectFraction(Aspect.ENTROPY, aspects));
        }
        if (aspects.getAmount(Aspect.METAL) > 0) {
            probabilities.put(Aspect.METAL, aspectFraction(Aspect.METAL, aspects));
        }
        if (aspects.getAmount(Aspect.CRYSTAL) > 0) {
            probabilities.put(Aspect.CRYSTAL, aspectFraction(Aspect.CRYSTAL, aspects));
        }
        if (aspects.getAmount(Aspect.SOUL) > 0) {
            probabilities.put(Aspect.SOUL, aspectFraction(Aspect.SOUL, aspects));
        }
        if (aspects.getAmount(Aspect.MAGIC) > 0) {
            probabilities.put(Aspect.MAGIC, aspectFraction(Aspect.MAGIC, aspects));
        }
        if (aspects.getAmount(Aspect.AURA) > 0) {
            probabilities.put(Aspect.AURA, aspectFraction(Aspect.AURA, aspects));
        }
        if (aspects.getAmount(Aspect.ENERGY) > 0) {
            probabilities.put(Aspect.ENERGY, aspectFraction(Aspect.ENERGY, aspects));
        }
        if (aspects.getAmount(Aspect.TREE) > 0) {
            probabilities.put(Aspect.TREE, aspectFraction(Aspect.TREE, aspects));
        }
        if (aspects.getAmount(Aspect.FLESH) > 0) {
            probabilities.put(Aspect.FLESH, aspectFraction(Aspect.FLESH, aspects));
        }
        if (aspects.getAmount(Aspect.SENSES) > 0) {
            probabilities.put(Aspect.SENSES, aspectFraction(Aspect.SENSES, aspects));
        }
        if (aspects.getAmount(Aspect.MINE) > 0) {
            probabilities.put(Aspect.MINE, aspectFraction(Aspect.MINE, aspects));
        }
        if (aspects.getAmount(Aspect.TOOL) > 0) {
            probabilities.put(Aspect.TOOL, aspectFraction(Aspect.TOOL, aspects));
        }
        if (aspects.getAmount(Aspect.GREED) > 0) {
            probabilities.put(Aspect.GREED, aspectFraction(Aspect.GREED, aspects));
        }
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            probabilities.put(Aspect.TAINT, aspectFraction(Aspect.TAINT, aspects));
        }
        if (aspects.getAmount(Aspect.POISON) > 0) {
            probabilities.put(Aspect.POISON, aspectFraction(Aspect.POISON, aspects));
        }
        if (aspects.getAmount(Aspect.EXCHANGE) > 0) {
            probabilities.put(Aspect.EXCHANGE, aspectFraction(Aspect.EXCHANGE, aspects));
        }
        if (aspects.getAmount(Aspect.MIND) > 0) {
            probabilities.put(Aspect.MIND, aspectFraction(Aspect.MIND, aspects));
        }
        if (aspects.getAmount(Aspect.MAN) > 0) {
            probabilities.put(Aspect.MAN, aspectFraction(Aspect.MAN, aspects));
        }
        if (aspects.getAmount(Aspect.HUNGER) > 0) {
            probabilities.put(Aspect.HUNGER, aspectFraction(Aspect.HUNGER, aspects));
        }
        if (aspects.getAmount(Aspect.CRAFT) > 0) {
            probabilities.put(Aspect.CRAFT, aspectFraction(Aspect.CRAFT, aspects));
        }
        TerrainBlock block = TerrainBlocks.air;
        int md = 0;
        for (int i = 0; i < numPockets; ++i) {
            float which = world.rand.nextFloat();
            Aspect whichAspect = null;
            final Iterator<Aspect> it = probabilities.keySet().iterator();
            boolean doDressing = false;
            while (it.hasNext()) {
                whichAspect = it.next();
                if (which <= probabilities.get(whichAspect).floatValue()) {
                    doDressing = true;
                    break;
                }
                which -= probabilities.get(whichAspect).floatValue();
            }
            if (doDressing && whichAspect != null) {
                if (whichAspect == Aspect.EARTH) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 4;
                } else if (whichAspect == Aspect.AIR) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 1;
                } else if (whichAspect == Aspect.WATER) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 3;
                } else if (whichAspect == Aspect.FIRE) {
                    if (world.rand.nextBoolean()) {
                        block = TerrainBlocks.blockCustomOre;
                        md = 2;
                    } else {
                        block = TerrainBlocks.lava;
                    }
                } else if (whichAspect == Aspect.ORDER) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 5;
                } else if (whichAspect == Aspect.ENTROPY) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 6;
                } else if (whichAspect == Aspect.METAL) {
                    if (world.rand.nextFloat() <= 0.5) {
                        block = TerrainBlocks.iron_ore;
                    } else if (world.rand.nextFloat() <= 0.8) {
                        block = TerrainBlocks.gold_ore;
                    } else {
                        block = TerrainBlocks.blockCustomOre;
                        md = 0;
                    }
                } else if (whichAspect == Aspect.MAGIC || whichAspect == Aspect.AURA) {
                    block = TerrainBlocks.blockCustomOre;
                    md = world.rand.nextInt(6) + 1;
                } else if (whichAspect == Aspect.CRYSTAL) {
                    block = TerrainBlocks.quartz_block;
                } else if (whichAspect == Aspect.SOUL) {
                    block = TerrainBlocks.soul_sand;
                } else if (whichAspect == Aspect.ENERGY) {
                    block = TerrainBlocks.redstone_block;
                } else if (whichAspect == Aspect.TREE) {
                    if (world.rand.nextBoolean()) {
                        block = TerrainBlocks.log;
                    } else {
                        block = TerrainBlocks.blockCustomOre;
                        md = 7;
                    }
                } else if (whichAspect == Aspect.FLESH) {
                    block = TerrainBlocks.blockTaint;
                    md = 2;
                } else if (whichAspect == Aspect.SENSES) {
                    block = TerrainBlocks.lapis_ore;
                } else if (whichAspect == Aspect.MINE || whichAspect == Aspect.TOOL) {
                    final float f = world.rand.nextFloat();
                    if (f < 0.4f) {
                        block = TerrainBlocks.iron_ore;
                    } else if (f < 0.5f) {
                        block = TerrainBlocks.gold_ore;
                    } else if (f < 0.6f) {
                        block = TerrainBlocks.diamond_ore;
                    } else if (f < 0.7f) {
                        block = TerrainBlocks.emerald_ore;
                    } else if (f < 0.8f) {
                        block = TerrainBlocks.lapis_ore;
                    } else {
                        block = TerrainBlocks.blockCustomOre;
                        md = world.rand.nextInt(8);
                    }
                } else if (whichAspect == Aspect.GREED) {
                    final float f = world.rand.nextFloat();
                    if (f < 0.4f) {
                        block = TerrainBlocks.gold_ore;
                    } else if (f < 0.6f) {
                        block = TerrainBlocks.diamond_ore;
                    } else if (f < 0.8f) {
                        block = TerrainBlocks.emerald_ore;
                    } else {
                        block = TerrainBlocks.lapis_ore;
                    }
                } else if (whichAspect == Aspect.TAINT) {
                    block = TerrainBlocks.blockTaint;
                    md = 0;
                } else if (whichAspect == Aspect.POISON) {
                    block = TerrainBlocks.blockFluidDeath;
                    md = 16;
                } else if (whichAspect == Aspect.EXCHANGE) {
                    block = TerrainBlocks.blockCustomOre;
                    md = 0;
                } else if (whichAspect == Aspect.MIND) {
                    if (world.rand.nextBoolean()) {
                        block = TerrainBlocks.bookshelf;
                    } else {
                        block = TerrainBlocks.blockBrain;
                    }
                } else if (whichAspect == Aspect.MAN) {
                    final int man = world.rand.nextInt(3);
                    if (man == 0) {
                        block = TerrainBlocks.bookshelf;
                    } else if (man == 1) {
                        block = TerrainBlocks.blockBrain;
                    } else {
                        block = TerrainBlocks.blockTaint;
                        md = 2;
                    }
                } else if (whichAspect == Aspect.HUNGER) {
                    block = TerrainBlocks.blockTaint;
                    md = 2;
                } else if (whichAspect == Aspect.CRAFT) {
                    block = TerrainBlocks.crafting_table;
                }
            }
            drawAPocket(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    aspects,
                    noise,
                    block,
                    md,
                    world.rand.nextInt(3) + 1,
                    world.rand.nextInt(3) + 1,
                    world.rand.nextInt((int) (data.radius * 2.0f)) - (int) (data.radius * 1.0f),
                    -world.rand.nextInt((int) (data.radius * 1.0f)),
                    world.rand.nextInt((int) (data.radius * 2.0f)) - (int) (data.radius * 1.0f));
        }
    }

    public void drawAPocket(final int xCenter, final int yCenter, final int zCenter, final Plan data,
            final TerrainWorld world, final AspectList aspects, final int noise, final TerrainBlock block, final int md,
            final int xSize, final int zSize, final int xOffset, final int yOffset, final int zOffset) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseDataTop = null;
        noiseDataTop = noiseGen.generateNoiseOctaves(
                noiseDataTop,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                1,
                zSize,
                noise / 50.0d,
                noise / 25.0d,
                noise / 50.0d);
        double[] noiseDataBottom = null;
        noiseDataBottom = noiseGen.generateNoiseOctaves(
                noiseDataBottom,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                1,
                zSize,
                noise / 50.0d,
                noise / 25.0d,
                noise / 50.0d);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                int y;
                for (int top = (int) (noiseDataTop[x + z * xSize] / 16.0) + 1,
                        bottom = y = (int) (noiseDataBottom[x + z * xSize] / 16.0) - 1; y < top; ++y) {
                    if (!world.isAirBlock(
                            xCenter - xSize + xOffset + x,
                            yCenter + yOffset + y,
                            zCenter - zSize + zOffset + z)
                            && world.getBlock(
                                    xCenter - xSize + xOffset + x,
                                    yCenter + yOffset + y,
                                    zCenter - zSize + zOffset + z) != TerrainBlocks.water) {
                        setBlock(
                                world,
                                xCenter - xSize + xOffset + x,
                                yCenter + yOffset + y,
                                zCenter - zSize + zOffset + z,
                                block,
                                md);
                    }
                }
            }
        }
    }

    public TerrainBiome setBiome(final int xCenter, final int yCenter, final int zCenter,
            final Plan data, final TerrainWorld world, final AspectList aspects) {
        TerrainBiome bio = null;
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            bio = TerrainBiome.taint;
        } else if (aspects.getAmount(Aspect.ELDRITCH) > 0 || aspects.getAmount(Aspect.UNDEAD) > 0) {
            bio = TerrainBiome.eerie;
        } else if (aspects.getAmount(Aspect.MAGIC) > 0 || aspects.getAmount(Aspect.AURA) > 0) {
            bio = TerrainBiome.magicalForest;
        } else {
            float temp = 0.5f;
            float moist = 0.5f;
            temp += aspectFraction(Aspect.FIRE, aspects);
            temp -= aspectFraction(Aspect.WATER, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.ORDER, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.VOID, aspects) * 0.5f;
            temp += aspectFraction(Aspect.LIGHT, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.WEATHER, aspects) * 0.25f;
            temp += aspectFraction(Aspect.MOTION, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.COLD, aspects);
            temp += aspectFraction(Aspect.ENERGY, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.DARKNESS, aspects) * 0.25f;
            if (temp < 0.0f) {
                temp = 0.0f;
            } else if (temp > 1.0f) {
                temp = 1.0f;
            }
            moist += aspectFraction(Aspect.WATER, aspects);
            moist -= aspectFraction(Aspect.FIRE, aspects) * 0.5f;
            moist -= aspectFraction(Aspect.ENTROPY, aspects) * 0.25f;
            moist -= aspectFraction(Aspect.VOID, aspects) * 0.25f;
            moist += aspectFraction(Aspect.WEATHER, aspects) * 0.5f;
            moist += aspectFraction(Aspect.SLIME, aspects) * 0.5f;
            if (moist < 0.0f) {
                moist = 0.0f;
            } else if (moist > 1.0f) {
                moist = 1.0f;
            }
            if (temp > 0.8) {
                bio = TerrainBiome.desert;
            } else if (temp > 0.5) {
                if (moist < 0.4) {
                    bio = TerrainBiome.savanna;
                } else {
                    bio = TerrainBiome.jungle;
                }
            } else if (temp > 0.2) {
                if (moist < 0.4) {
                    bio = TerrainBiome.plains;
                } else {
                    bio = TerrainBiome.forest;
                }
            } else {
                bio = TerrainBiome.icePlains;
            }
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                world.setBiome(bio);
            }
        }
        return bio;
    }

    public void drawCircle(final int x0, final int y0, final int z0, final int y1, final int radius,
            final int error0, final TerrainBlock block, final int md, final TerrainWorld world) {
        int x = radius;
        int z = 0;
        int radiusError = error0;
        while (x >= z) {
            setBlock(world, x0 + x, y0 + y1, z0 + z, block, md);
            setBlock(world, x0 + z, y0 + y1, z0 + x, block, md);
            setBlock(world, x0 - x, y0 + y1, z0 + z, block, md);
            setBlock(world, x0 - z, y0 + y1, z0 + x, block, md);
            setBlock(world, x0 - x, y0 + y1, z0 - z, block, md);
            setBlock(world, x0 - z, y0 + y1, z0 - x, block, md);
            setBlock(world, x0 + x, y0 + y1, z0 - z, block, md);
            setBlock(world, x0 + z, y0 + y1, z0 - x, block, md);
            setBlock(world, x0 + x, y0 - y1, z0 + z, block, md);
            setBlock(world, x0 + z, y0 - y1, z0 + x, block, md);
            setBlock(world, x0 - x, y0 - y1, z0 + z, block, md);
            setBlock(world, x0 - z, y0 - y1, z0 + x, block, md);
            setBlock(world, x0 - x, y0 - y1, z0 - z, block, md);
            setBlock(world, x0 - z, y0 - y1, z0 - x, block, md);
            setBlock(world, x0 + x, y0 - y1, z0 - z, block, md);
            setBlock(world, x0 + z, y0 - y1, z0 - x, block, md);
            setBlock(world, x0 + y1, y0 + x, z0 + z, block, md);
            setBlock(world, x0 + z, y0 + x, z0 + y1, block, md);
            setBlock(world, x0 - y1, y0 + x, z0 + z, block, md);
            setBlock(world, x0 - z, y0 + x, z0 + y1, block, md);
            setBlock(world, x0 + y1, y0 + x, z0 - z, block, md);
            setBlock(world, x0 + z, y0 + x, z0 - y1, block, md);
            setBlock(world, x0 - y1, y0 + x, z0 - z, block, md);
            setBlock(world, x0 - z, y0 + x, z0 - y1, block, md);
            setBlock(world, x0 + y1, y0 - x, z0 + z, block, md);
            setBlock(world, x0 + z, y0 - x, z0 + y1, block, md);
            setBlock(world, x0 - y1, y0 - x, z0 + z, block, md);
            setBlock(world, x0 - z, y0 - x, z0 + y1, block, md);
            setBlock(world, x0 + y1, y0 - x, z0 - z, block, md);
            setBlock(world, x0 + z, y0 - x, z0 - y1, block, md);
            setBlock(world, x0 - y1, y0 - x, z0 - z, block, md);
            setBlock(world, x0 - z, y0 - x, z0 - y1, block, md);
            ++z;
            if (radiusError < 0) {
                radiusError += 2 * z + 1;
            } else {
                --x;
                radiusError += 2 * (z - x + 1);
            }
        }
    }

    public void drawSphere(final int x0, final int y0, final int z0, final int radius, final TerrainBlock block,
            final int md, final TerrainWorld world) {
        int x = radius;
        int y = 0;
        int radiusError = 1 - x;
        while (x >= y) {
            drawCircle(x0, y0, z0, y, x, radiusError, block, md, world);
            ++y;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                --x;
                radiusError += 2 * (y - x + 1);
            }
        }
    }


}
