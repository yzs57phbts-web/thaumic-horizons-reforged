package thaumichorizons;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
final class PocketDimension {
    static final ResourceKey<Level> KEY=ResourceKey.create(Registries.DIMENSION,ThaumicHorizons.id("pocket_plane"));
    private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS=DeferredRegister.create(Registries.CHUNK_GENERATOR,ThaumicHorizons.ID);
    static void init(IEventBus bus){GENERATORS.register("pocket_plane",()->PocketChunkGenerator.CODEC);GENERATORS.register(bus);}
}
