//package raccoonman.reterraforged.client.gui.screen.presetconfig;
//
//import java.util.regex.Pattern;
//
//import org.jetbrains.annotations.Nullable;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.datafixers.util.Unit;
//import com.mojang.serialization.DataResult;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.EditBox;
//import net.minecraft.client.gui.components.toasts.SystemToast;
//import net.minecraft.client.gui.components.toasts.ToastComponent;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
//import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
//import net.minecraft.network.chat.CommonComponents;
//import net.minecraft.network.chat.Component;
//import raccoonman.reterraforged.client.data.RTFTranslationKeys;
//import raccoonman.reterraforged.client.gui.ColorValidator;
//import raccoonman.reterraforged.client.gui.ColumnAlignment;
//import raccoonman.reterraforged.client.gui.page.Page;
//import raccoonman.reterraforged.client.gui.page.PageList;
//import raccoonman.reterraforged.client.gui.widget.Label;
//import raccoonman.reterraforged.client.gui.widget.WidgetList;
//import raccoonman.reterraforged.client.preset.Preset;
//
//public class PresetConfigScreen extends Screen {
//	private CreateWorldScreen parent;
//	private PageList pageList;
//	private Button next;
//	@Nullable
//	private Preset selected;
//	
//	public PresetConfigScreen(CreateWorldScreen parent, WorldCreationContext settings) {
//		super(CommonComponents.EMPTY);
//		
//		this.parent = parent;
//		this.pageList = new PageList(
//			new PresetsPage(),
//			new WorldSettings(this)
//		);
//	}
//
//	@Override
//	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
//		super.renderBackground(poseStack);
//		super.render(poseStack, mouseX, mouseY, partialTicks);
//	}
//	
//	@Override
//	protected void init() {
//		super.init();
//
//        int buttonsCenter = this.width / 2;
//        int buttonWidth = 50;
//        int buttonHeight = 20;
//        int buttonPad = 2;
//        int buttonsRow = this.height - 25;
//
//		Page current = this.pageList.init();
//		this.addRenderableOnly(new Label(16, 15, 20, 20, current.title()));
//		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (b) -> {
//			this.popScreen();
//		}).bounds(buttonsCenter - buttonWidth - buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
//		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (b) -> {
//			this.popScreen();
//		}).bounds(buttonsCenter + buttonPad, buttonsRow, buttonWidth, buttonHeight).build());
//		this.tryActivatePrevious(
//			this.addRenderableWidget(Button.builder(Component.literal("<<"), this::previousPage).bounds(buttonsCenter - (buttonWidth * 2 + (buttonPad * 3)), buttonsRow, buttonWidth, buttonHeight).build())
//		);
//
//		this.next = this.addRenderableWidget(Button.builder(Component.literal(">>"), this::nextPage).bounds(buttonsCenter + buttonWidth + (buttonPad * 3), buttonsRow, buttonWidth, buttonHeight).build());
//		PresetConfigScreen.this.next.active = !this.pageList.atBottom() // <- hacky way to disable only if we are on the preset page
//			&& !this.pageList.atTop();
//	}
//
//	private void popScreen() {
//		this.minecraft.setScreen(this.parent);
//	}
//	
//	private void previousPage(Button b) {
//		this.pageList.previous();
//		this.tryActivatePrevious(b);
//		this.rebuildWidgets();
//	}
//	
//	private void nextPage(Button b) {
//		this.pageList.next();
//		this.tryActivateNext(b);
//		this.rebuildWidgets();
//	}
//	
//	private void tryActivatePrevious(Button b) {
//		b.active = !this.pageList.atBottom();
//	}
//	
//	private void tryActivateNext(Button b) {
//		b.active = !this.pageList.atTop();
//	}
//	
//	static abstract class SubPage implements Page {
//		protected WidgetList left;
//		protected WidgetList right;
//		protected PresetConfigScreen parent;
//		private Component title;
//		
//		public SubPage(PresetConfigScreen parent, Component title) {
//			this.parent = parent;
//			this.title = title;
//		}
//		
//		@Override
//		public Component title() {
//			return this.title;
//		}
//		
//		@Override
//		public void init() {
//			ColumnAlignment alignment = new ColumnAlignment(this.parent, 4, 0, 10, 30);
//			this.left = alignment.addColumn(0.7F, this::createAndPositionColumn);
//			this.right = alignment.addColumn(0.3F, this::createAndPositionColumn);
//		}
//		
//		private WidgetList createAndPositionColumn(int left, int top, int columnWidth, int height, int horizontalPadding, int verticalPadding) {
//			final int padding = 30;
//			final int slotHeight = 25;
//			WidgetList list = new WidgetList(this.parent.minecraft, columnWidth, height, padding, height - padding, slotHeight);
//			list.setLeftPos(left);
//			return list;
//		}
//	}
//	
//	private class PresetsPage extends SubPage {
////		private PresetManager presetManager;
//		
//		public PresetsPage() {
//			super(PresetConfigScreen.this, Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_TITLE));
////
////			this.presetManager = new PresetManager(ReTerraForged.getConfigPath().resolve("presets"));
////			notify(this.presetManager.load());
//		}
//
//		@Override
//		public void init() {
//			super.init();
//
//			this.left.setRenderSelection(true);
//			
//			EditBox box = this.right.addWidget(new EditBox(PresetConfigScreen.this.font, 0, 0, 100, 20, CommonComponents.EMPTY));
//			Button create = this.right.addWidget(Button.builder(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_CREATE), (b) -> {
////				notify(this.presetManager.register(box.getValue())).result().ifPresent((v) -> {
////					PresetConfigScreen.this.rebuildWidgets();
////				});
//			}).build());
//			ColorValidator validator = new ColorValidator(Pattern.compile("^[A-Za-z0-9\\-_ ]+$").asPredicate(), box::setTextColor, 14737632, 0xffff3f30);
//			box.setResponder((text) -> {
////				create.active = !text.isBlank() && !this.presetManager.hasPreset(text);
////				
////				validator.accept(text);
//			});
//			create.active = !box.getValue().isBlank();
//			
//			Button save = this.right.addWidget(Button.builder(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_SAVE), (b) -> {
//				
//			}).build());
//			Button reset = this.right.addWidget(Button.builder(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_RESET), (b) -> {
//				
//			}).build());
//			Button delete = this.right.addWidget(Button.builder(Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_DELETE), (b) -> {
////				notify(this.presetManager.delete(this.left.getSelected().getWidget().getMessage().getString())).result().ifPresent((v) -> {
////					PresetConfigScreen.this.rebuildWidgets();
////				});
//			}).build());
//
//			save.active = false;
//			reset.active = false;
//			delete.active = false;
////			
////			notify(this.presetManager.load()).result().ifPresent((v) -> {
////				for(Entry<String, Preset> presets : this.presetManager.presets().entrySet()) {
////					this.left.addWidget(new Label(0, 0, 20, 20, (b) -> {
////						this.left.select(b);
////						save.active = true;
////						reset.active = true;
////						delete.active = true;
////						PresetConfigScreen.this.next.active = true;
////					}, Component.literal(presets.getKey())));
////				}
////			});
//		}
//
//		private static DataResult<Unit> notify(DataResult<Unit> result) {
//			result.error().ifPresent((error) -> {
//				ToastComponent toasts = Minecraft.getInstance().getToasts();
//		        SystemToast.addOrUpdate(toasts, SystemToast.SystemToastIds.PACK_LOAD_FAILURE, Component.translatable(RTFTranslationKeys.PRESET_CONFIG_PRESETS_LOAD_FAIL), null);
//			});
//			return result;
//		}
//	}
//}