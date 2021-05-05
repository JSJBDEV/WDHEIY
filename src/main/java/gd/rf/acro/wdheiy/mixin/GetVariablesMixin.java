package gd.rf.acro.wdheiy.mixin;


import io.github.minecraftcursedlegacy.api.registry.Registries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.screen.container.PlayerInventoryScreen;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.gui.widgets.Textbox;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.item.ItemInstance;
import net.minecraft.item.ItemType;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(Screen.class)
public abstract class GetVariablesMixin {
    @Shadow protected List buttons;


    @Shadow public abstract void updateServer(boolean flag, int i);

    private int page = 10;
    private Minecraft minecraft;
    private ItemRenderer renderer = new ItemRenderer();
    private Textbox in;
    private String text = "";
    @Inject(at = @At("TAIL"), method = "init(Lnet/minecraft/client/Minecraft;II)V")
    public void init(Minecraft minecraft, int width, int height, CallbackInfo callbackInfo)
    {
        this.minecraft=minecraft;
        if(minecraft.currentScreen instanceof PlayerInventoryScreen)
        {
            addButtonsTo();
        }

    }

    @Inject(at = @At("HEAD"), method = "mouseClicked")
    private void mouseClicked(int mouseX, int mouseY, int button,CallbackInfo callbackInfo) {
        if(minecraft.currentScreen instanceof PlayerInventoryScreen)
        {
            if((mouseY-10)/20>=this.buttons.size())
            {
                return;
            }
            Button c = (Button) this.buttons.get((mouseY-10)/20);
            if(c.text.equals("<") && mouseX>50)
            {

                page+=10;
                addButtonsTo();
                return;
            }
            if(c.text.equals("<"))
            {
                if(page-10<1)
                {
                    return;
                }
                page-=10;
                addButtonsTo();
                return;
            }
            if(mouseX < 100)
            {
                if(mouseY>230)
                {
                    in.method_1879(mouseX, mouseY, button);
                }
                System.out.println(c.text);
                ItemType e = null;
                if(c.text.split(":").length<2) return;
                for (ItemType v : Registries.ITEM_TYPE) {
                    if (v.id==Integer.parseInt(c.text.split(":")[1])) {
                        e=v;
                        break;
                    }
                }
                int count = 1;
                int damage = 0;
                if(in.method_1876().contains(","))
                {
                    String[] v =in.method_1876().split(",");
                    count=Integer.parseInt(v[1]);
                }
                if(in.method_1876().contains(";"))
                {
                    String[] v =in.method_1876().split(";");
                    damage=Integer.parseInt(v[1]);
                }
                System.out.println(e);
                this.minecraft.player.inventory.pickupItem(new ItemInstance(e,count,damage));

            }


        }
    }

    @Inject(at = @At("HEAD"), method = "onKeyboardEvent",cancellable = true)
    private void keyPressed(CallbackInfo callbackInfo) {
        System.out.println(Keyboard.getKeyName(Keyboard.getEventKey()));
        if(minecraft.currentScreen instanceof PlayerInventoryScreen)
        {
            in.method_1877(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            text=in.method_1876();
            page=10;
            addButtonsTo();

            if(Keyboard.getKeyName(Keyboard.getEventKey()).equals("E"))
            {
                callbackInfo.cancel();
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "render(IIF)V")
    public void render(int mouseX, int mouseY, float delta,CallbackInfo callbackInfo)
    {
        if(minecraft.currentScreen instanceof PlayerInventoryScreen)
        {
            minecraft.textRenderer.drawText("WDHEIY v1.0",1,1, 16777215);
            in.method_1883();

        }
    }

    private void addButtonsTo()
    {
        this.buttons.clear();
        int d =0;
        in=new Textbox(minecraft.currentScreen,minecraft.textRenderer,0,230,100,20,text);
        in.field_2420 = true;
        in.method_1878(32);
        List<ItemType> items = new ArrayList<>( Registries.ITEM_TYPE.values()).stream().filter(t->
        {
            String searched = in.method_1876().toLowerCase().split(":")[0];
            if(searched.contains(","))
            {
                searched=searched.split(",")[0];
            }
            if(searched.contains(";"))
            {
                searched=searched.split(";")[0];
            }
            if(t.getTranslatedName().toLowerCase().contains(searched))
            {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        for (int i = page-10; i < page; i++) {
            if(i>=items.size())
            {
                return;
            }
            this.buttons.add(new OptionButton(5, 0, 10+d,100,20, items.get(i).getTranslatedName()+":"+items.get(i).id));
            d+=20;
        }
        this.buttons.add(new OptionButton(5, 0, 210,50,20, "<"));
        this.buttons.add(new OptionButton(5, 50, 210,50,20, ">"));


    }



}
