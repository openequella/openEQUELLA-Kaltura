/*
 * Copyright 2017 Apereo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tle.web.controls.kaltura;

import com.kaltura.client.types.ListResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.kaltura.client.types.APIException;
import com.kaltura.client.Client;
import com.kaltura.client.enums.SessionType;
import com.kaltura.client.types.MediaEntry;
import com.kaltura.client.types.UiConf;
import com.tle.annotation.NonNullByDefault;
import com.tle.annotation.Nullable;
import com.tle.beans.item.attachments.Attachment;
import com.tle.beans.item.attachments.CustomAttachment;
import com.tle.beans.item.attachments.IAttachment;
import com.tle.common.Check;
import com.tle.common.Utils;
import com.tle.common.kaltura.KalturaUtils;
import com.tle.common.kaltura.admin.control.KalturaSettings;
import com.tle.common.kaltura.admin.control.KalturaSettings.KalturaOption;
import com.tle.common.kaltura.entity.KalturaServer;
import com.tle.core.guice.Bind;
import com.tle.core.kaltura.service.KalturaService;
import com.tle.web.controls.universal.AbstractDetailsAttachmentHandler;
import com.tle.web.controls.universal.AttachmentHandlerLabel;
import com.tle.web.controls.universal.BasicAbstractAttachmentHandler;
import com.tle.web.controls.universal.DialogRenderOptions;
import com.tle.web.controls.universal.UniversalControlState;
import com.tle.core.i18n.BundleCache;
import com.tle.web.resources.ResourcesService;
import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.SectionTree;
import com.tle.web.sections.SectionUtils;
import com.tle.web.sections.ajax.AjaxGenerator;
import com.tle.web.sections.ajax.handler.AjaxFactory;
import com.tle.web.sections.annotations.Bookmarked;
import com.tle.web.sections.annotations.EventHandlerMethod;
import com.tle.web.sections.equella.annotation.PlugKey;
import com.tle.web.sections.equella.annotation.PlugURL;
import com.tle.web.sections.equella.listmodel.EnumListModel;
import com.tle.web.sections.equella.render.DateRendererFactory;
import com.tle.web.sections.equella.utils.VoidKeyOption;
import com.tle.web.sections.events.RenderContext;
import com.tle.web.sections.events.js.JSHandler;
import com.tle.web.sections.jquery.libraries.JQueryUICore;
import com.tle.web.sections.js.JSCallable;
import com.tle.web.sections.js.generic.Js;
import com.tle.web.sections.js.generic.OverrideHandler;
import com.tle.web.sections.js.generic.ReloadHandler;
import com.tle.web.sections.js.generic.StatementHandler;
import com.tle.web.sections.js.generic.expression.FunctionCallExpression;
import com.tle.web.sections.js.generic.expression.NotEqualsExpression;
import com.tle.web.sections.js.generic.expression.StringExpression;
import com.tle.web.sections.js.generic.function.ExternallyDefinedFunction;
import com.tle.web.sections.js.generic.function.IncludeFile;
import com.tle.web.sections.js.generic.statement.FunctionCallStatement;
import com.tle.web.sections.js.validators.SimpleValidator;
import com.tle.web.sections.render.CombinedRenderer;
import com.tle.web.sections.render.CssInclude;
import com.tle.web.sections.render.Label;
import com.tle.web.sections.render.LabelRenderer;
import com.tle.web.sections.render.SectionRenderable;
import com.tle.web.sections.render.TagState;
import com.tle.web.sections.render.TextLabel;
import com.tle.web.sections.result.util.BundleLabel;
import com.tle.web.sections.result.util.KeyLabel;
import com.tle.web.sections.result.util.NumberLabel;
import com.tle.web.sections.standard.Button;
import com.tle.web.sections.standard.Div;
import com.tle.web.sections.standard.Link;
import com.tle.web.sections.standard.MultiSelectionList;
import com.tle.web.sections.standard.Pager;
import com.tle.web.sections.standard.SingleSelectionList;
import com.tle.web.sections.standard.TextField;
import com.tle.web.sections.standard.annotations.Component;
import com.tle.web.sections.standard.model.DynamicHtmlListModel;
import com.tle.web.sections.standard.model.HtmlLinkState;
import com.tle.web.sections.standard.model.LabelOption;
import com.tle.web.sections.standard.model.Option;
import com.tle.web.sections.standard.model.SimpleBookmark;
import com.tle.web.sections.standard.model.SimpleOption;
import com.tle.web.sections.standard.renderers.HeadingRenderer;
import com.tle.web.sections.standard.renderers.ImageRenderer;
import com.tle.web.sections.standard.renderers.LinkRenderer;
import com.tle.web.sections.standard.renderers.popup.PopupLinkRenderer;
import com.tle.web.viewurl.ItemSectionInfo;
import com.tle.web.viewurl.ViewableResource;
import com.tle.web.viewurl.attachments.AttachmentResourceService;

@Bind
@NonNullByDefault
@SuppressWarnings("nls")
public class KalturaHandler extends BasicAbstractAttachmentHandler<KalturaHandler.KalturaHandlerModel>
{
	private static final int PER_PAGE = 10;

	private static final CssInclude CSS = CssInclude
			.include(ResourcesService.getResourceHelper(KalturaHandler.class).url("css/kaltura.css")).hasRtl().make();
	private static final CssInclude UPLOAD_CONTROL_CSS = CssInclude
			.include(ResourcesService.getResourceHelper(KalturaHandler.class).url("js/UploadControlEntry.css")).hasRtl().make();
	@PlugURL("images/kalturalogotrans.png")
	private static String KALTURA_LOGO_URL;

	@PlugURL("js/UploadControlEntry.js")
	private static String UPLOAD_CONTROL;
	@PlugURL("js/kalturaopts.js")
	private static String KALTURA_OPTS;

	@PlugKey("choice.")
	private static String KEY_PREFIX_CHOICES;

	@PlugKey("name")
	private static Label NAME_LABEL;
	@PlugKey("description")
	private static Label DESCRIPTION_LABEL;

	@PlugKey("add.title")
	private static Label ADD_TITLE_LABEL;
	@PlugKey("add.views.singular")
	private static String SINGULAR_VIEWS_LABEL;
	@PlugKey("add.views.plural")
	private static String PLURAL_VIEWS_LABEL;
	@PlugKey("add.query.empty")
	private static Label EMPTY_QUERY_LABEL;
	@PlugKey("edit.title")
	private static Label EDIT_TITLE_LABEL;

	@PlugKey("uploaded.tags")
	private static String UPLOAD_TAGS_LABEL;
	@PlugKey("details.views")
	private static Label VIEWS_LABEL;
	@PlugKey("details.viewlink")
	private static Label VIEW_LINK_LABEL;
	@PlugKey("details.downloadlink")
	private static Label DOWNLOAD_LINK_LABEL;
	@PlugKey("details.duration.seconds.singular")
	private static Label SECONDS_SINGULAR;
	@PlugKey("details.duration.seconds.plural")
	private static String SECONDS_PLURAL;
	@PlugKey("details.duration")
	private static Label DURATION;

	@PlugKey("info.unavailable")
	private static String KEY_UNAVAILABLE;
	@PlugKey("info.unavailable.desc")
	private static String KEY_UNAVAILABLE_DESC;
	@PlugKey("info.")
	private static String KEY_INFO_PREFIX;
	@PlugKey("info.desc.")
	private static String KEY_INFO_DESC_PREFIX;

	@PlugKey("edit.players.default")
	private static Label SERVER_DEFAULT;

	private JSCallable setupKalturaUpload;
	private JSCallable finishedCallback;

	@Inject
	private KalturaService kalturaService;
	@Inject
	private BundleCache bundleCache;
	@Inject
	private DateRendererFactory dateRendererFactory;
	@Inject
	private AttachmentResourceService attachmentResourceService;

	@AjaxFactory
	private AjaxGenerator ajax;

	@Component
	private TextField query;
	@Component
	@PlugKey("add.search.button")
	private Button search;

	@Component(name = "confid")
	private SingleSelectionList<UiConf> players;

	@PlugKey("action.next")
	@Component
	private Button nextChoiceButton;

	@Component
	private MultiSelectionList<Void> results;
	@Component
	private Pager pager;
	@Component
	private MultiSelectionList<KalturaUpload> selections;
	@PlugKey("uploaded.link.selectall")
	@Component
	private Link selectAll;
	@PlugKey("uploaded.link.selectnone")
	@Component
	private Link selectNone;

	@Component
	private SingleSelectionList<KalturaOption> choice;

	@Component
	private Div divKcw;
	@Component
	private Div divKdp;

	private KalturaSettings kalturaSettings;

	@Override
	public void onRegister(SectionTree tree, String parentId, UniversalControlState state)
	{
		super.onRegister(tree, parentId, state);
		kalturaSettings = new KalturaSettings(state.getControlConfiguration());
	}

	@Override
	public void registered(String id, SectionTree tree)
	{
		super.registered(id, tree);
		choice.setListModel(new EnumListModel<KalturaOption>(KEY_PREFIX_CHOICES, ".desc", true, KalturaOption.EXISTING,
				KalturaOption.UPLOAD));

		search.setClickHandler(new OverrideHandler(events.getNamedHandler("searchClicked")).addValidator(
				new SimpleValidator(new NotEqualsExpression(query.createGetExpression(), new StringExpression("")))
						.setFailureStatements(Js.alert_s(EMPTY_QUERY_LABEL))));

		nextChoiceButton.setClickHandler(new ReloadHandler());
		setupKalturaUpload = new ExternallyDefinedFunction("KalturaUploadaControl", new IncludeFile(
				UPLOAD_CONTROL));
		finishedCallback = events.getSubmitValuesFunction("finished");

		results.setListModel(new DynamicHtmlListModel<Void>()
		{
			@Override
			protected Iterable<Option<Void>> populateOptions(SectionInfo info)
			{
				KalturaServer ks = getKalturaServer();
				KalturaHandlerModel model = getModel(info);

				if( model.isButtonUpdate() )
				{
					return Collections.emptyList();
				}

				String value = query.getValue(info);
				String q = !Check.isEmpty(value) ? value : "";
				if( Check.isEmpty(q) )
				{
					return null;
				}

				ListResponse<MediaEntry> mediaList = kalturaService.searchMedia(
						getKalturaClient(ks, SessionType.ADMIN), Lists.newArrayList(q), pager.getCurrentPage(info),
						PER_PAGE);

				if( mediaList == null || mediaList.getTotalCount() == 0 )
				{
					return null;
				}

				pager.setup(info, (mediaList.getTotalCount() - 1) / PER_PAGE + 1, PER_PAGE);

				final List<Option<Void>> rv = new ArrayList<Option<Void>>();

				for( MediaEntry entry : mediaList.getObjects() )
				{
					KalturaResultOption result = new KalturaResultOption(entry.getId());

					Attachment attachment = createAttachment(entry.getId());
					// Use iframe embed here.
					String embedUrl = kalturaService.createPlayerEmbedUrl(
							attachment, kalturaService.kalturaPlayerId(), false, null);
					final LinkRenderer titleLink = new PopupLinkRenderer(new HtmlLinkState(new SimpleBookmark(embedUrl)));

					titleLink.setLabel(new TextLabel(entry.getName()));
					result.setLink(titleLink);
					result.setDescription(new TextLabel(entry.getDescription()));
					result.setDate(dateRendererFactory.createDateRenderer(new Date(entry.getCreatedAt() * 1000L)));
					result.setThumbnail(new ImageRenderer(entry.getThumbnailUrl(), new TextLabel(entry.getName())));
					int views = entry.getViews();
					result.setViews(
							views == 1 ? new KeyLabel(SINGULAR_VIEWS_LABEL) : new KeyLabel(PLURAL_VIEWS_LABEL, views));

					rv.add(result);
				}
				return rv;
			}

			@Override
			protected Iterable<Void> populateModel(SectionInfo info)
			{
				return null;
			}
		});

		players.setListModel(new DynamicHtmlListModel<UiConf>()
		{
			@Override
			protected Iterable<UiConf> populateModel(SectionInfo info)
			{
				return kalturaService.getPlayers(getKalturaServer());
			}

			@Override
			protected Option<UiConf> getTopOption()
			{
				return new LabelOption<UiConf>(SERVER_DEFAULT, "", null);
			}

			@Override
			protected Option<UiConf> convertToOption(SectionInfo info, UiConf conf)
			{
				return new SimpleOption<UiConf>(conf.getName(), Integer.toString(conf.getId()), conf);
			}
		});

		players.addChangeEventHandler(ajax.getAjaxUpdateDomFunction(tree, null, null, "mediapreview"));

		pager.setEventHandler(JSHandler.EVENT_CHANGE, new ReloadHandler());
	}

	@Override
	public void treeFinished(String id, SectionTree tree)
	{
		super.treeFinished(id, tree);

		StatementHandler updateHandler = new StatementHandler(
				dialogState.getDialog().getFooterUpdate(tree, events.getEventHandler("updateButtons")));

		results.setEventHandler(JSHandler.EVENT_CHANGE, updateHandler);
	}

	public Client getKalturaClient(KalturaServer ks, SessionType type)
	{
		try
		{
			return kalturaService.getKalturaClient(ks, type);
		}
		catch( APIException e )
		{
			SectionUtils.throwRuntime(e);
		}

		return null;
	}

	@Nullable
	private KalturaServer getKalturaServer()
	{
		return kalturaService.getByUuid(kalturaSettings.getServerUuid());
	}

	@Override
	protected boolean isOnePageAdd()
	{
		return false;
	}

	@Override
	protected SectionRenderable renderAdd(RenderContext context, DialogRenderOptions renderOptions)
	{
		KalturaHandlerModel model = getModel(context);
		SectionRenderable renderable;

		KalturaServer ks = getKalturaServer();
		if( ks == null || !ks.isEnabled() || !kalturaService.isUp(ks) )
		{
			String key = ks == null ? "missing" : !ks.isEnabled() ? "disabled" : "offline";
			HeadingRenderer heading = new HeadingRenderer(3,
					new KeyLabel(KEY_UNAVAILABLE, new KeyLabel(KEY_INFO_PREFIX + key)));
			LabelRenderer error = new LabelRenderer(
					new KeyLabel(KEY_UNAVAILABLE_DESC, new KeyLabel(KEY_INFO_DESC_PREFIX + key)));

			ImageRenderer watermark = new ImageRenderer(new TagState("kaltura-logo"), KALTURA_LOGO_URL,
					new TextLabel("kalturalogo"));

			return new CombinedRenderer(heading, error, watermark, CSS);
		}

		model.setKalturaServer(new BundleLabel(ks.getName().getId(), bundleCache));

		if( !isMultipleAllowed(context) )
		{
			results.getState(context).setDisallowMultiple(true);
		}

		// Check if there is a restriction on the choice
		String restriction = kalturaSettings.getRestriction();
		KalturaOption choiceOption = null;

		if( restriction != null )
		{
			choiceOption = KalturaOption.valueOf(restriction);
		}
		else
		{
			choiceOption = choice.getSelectedValue(context);
		}

		if( choiceOption == null )
		{
			// Render Choice
			renderable = renderChoice(context, renderOptions);
		}
		else
		{
			// Render KCW or Search
			if( choiceOption == KalturaOption.EXISTING )
			{
				renderable = renderSearch(context, renderOptions);
			}
			else
			{
				setupKalturaKcw(context);
				renderable = new CombinedRenderer(renderContribution(), UPLOAD_CONTROL_CSS);
			}
		}

		return new CombinedRenderer(renderable, CSS);
	}

	private void setupKalturaKcw(RenderContext context)
	{
		KalturaServer ks = getKalturaServer();
		divKcw.addReadyStatements(
				context, new FunctionCallStatement(
						new FunctionCallExpression(setupKalturaUpload,
								divKcw.getElementId(context),
								getKalturaClient(ks, SessionType.USER).getSessionId(),
								ks.getPartnerId(),
								finishedCallback)));
	}

	private void setupKalturaPlayer(SectionInfo context, IAttachment attachment)
	{
		KalturaHandlerModel model = getModel(context);
		String playerId = kalturaService.kalturaPlayerId();
		model.setPlayerId(playerId);
		model.setViewerUrl(kalturaService.createPlayerEmbedUrl(attachment, playerId, true, players.getSelectedValueAsString(context)));
	}

	private SectionRenderable renderChoice(RenderContext context, DialogRenderOptions renderOptions)
	{
		renderOptions.addAction(nextChoiceButton);
		choice.addReadyStatements(context,
				new ExternallyDefinedFunction("setupOpts", new IncludeFile(KALTURA_OPTS, JQueryUICore.PRERENDER)),
				nextChoiceButton.getState(context));
		getModel(context).setKalturaLogo(
				new ImageRenderer(new TagState("kaltura-logo"), KALTURA_LOGO_URL, new TextLabel("kalturalogo")));
		return viewFactory.createResult("option-kaltura.ftl", this);
	}

	private SectionRenderable renderSearch(RenderContext context, DialogRenderOptions renderOptions)
	{
		renderOptions.setShowSave(!Check.isEmpty(results.getSelectedValuesAsStrings(context)));
		renderOptions.setShowAddReplace(true);

		return viewFactory.createResult("add-kaltura.ftl", this);
	}

	@Override
	protected SectionRenderable renderDetails(RenderContext context, DialogRenderOptions renderOptions)
	{
		KalturaServer ks = getKalturaServer();

		KalturaHandlerModel model = getModel(context);
		final Attachment a = getDetailsAttachment(context);
		model.setShowPlayers(players.getListModel().getOptions(context).size() > 1);

		// Setup preview embed
		setupKalturaPlayer(context, a);

		// Get common details from viewable resource
		ItemSectionInfo itemInfo = context.getAttributeForClass(ItemSectionInfo.class);
		ViewableResource resource = attachmentResourceService.getViewableResource(context, itemInfo.getViewableItem(),
				a);
		addAttachmentDetails(context, resource.getCommonAttachmentDetails());

		// Get dynamic details
		String entryId = (String) a.getData(KalturaUtils.PROPERTY_ENTRY_ID);
		if( !Check.isEmpty(entryId) )
		{
			// Get kaltura media entry
			MediaEntry entry = kalturaService.getMediaEntry(getKalturaClient(ks, SessionType.ADMIN),
					entryId);

			// Duration has to be dynamic as it is 0 when converting
			int duration = entry.getDuration();
			if( duration != Integer.MIN_VALUE )
			{
				// Cannot cast from Integer to long
				String fd = Utils.formatDuration(duration);
				addAttachmentDetail(context, DURATION, fd.contains(":") ? new TextLabel(fd)
						: (fd.equals("1") ? SECONDS_SINGULAR : new KeyLabel(SECONDS_PLURAL, fd)));
			}

			addAttachmentDetail(context, VIEWS_LABEL, new NumberLabel(entry.getViews()));

			HtmlLinkState linkState;
			String downloadUrl = entry.getDownloadUrl();
			if( !Check.isEmpty(downloadUrl) )
			{
				linkState = new HtmlLinkState(DOWNLOAD_LINK_LABEL, new SimpleBookmark(downloadUrl));
			}
			else
			{
				linkState = new HtmlLinkState(VIEW_LINK_LABEL, new SimpleBookmark(""));
			}
			linkState.setTarget(HtmlLinkState.TARGET_BLANK);
			model.setViewlink(new LinkRenderer(linkState));
		}

		return new CombinedRenderer(viewFactory.createResult("edit-kaltura.ftl", this), CSS);
	}

	private SectionRenderable renderContribution()
	{
		return viewFactory.createResult("contribute-kaltura.ftl", this);
	}

	private String createHtml5embed(KalturaServer ks)
	{
		return MessageFormat.format("{0}/p/{1}/embedIframeJs/uiconf_id/{2}/partner_id/{1}", ks.getEndPoint(),
				Integer.toString(ks.getPartnerId()), Integer.toString(ks.getKdpUiConfId()));
	}

	@EventHandlerMethod
	public void finished(SectionInfo info, List<KalturaUploadInfo> entries)
	{
		KalturaHandlerModel model = getModel(info);
		model.setFinishedUploading(true);
		model.addUploads(entries);

		if( entries.size() == 1 )
		{
			selections.setSelectedStringValue(info, entries.get(0).getId());
			dialogState.save(info);
		}
	}

	@EventHandlerMethod
	public void searchClicked(SectionInfo info)
	{
		getModel(info).setSearchPerformed(!Check.isEmpty(query.getValue(info)));
	}

	public static class KalturaUploadInfo
	{
		private String mediaType;
		private String id;

		public KalturaUploadInfo()
		{
			// Nothing to see here
		}

		public KalturaUploadInfo(String mediaType, String id)
		{
			this.mediaType = mediaType;
			this.id = id;
		}

		public String getMediaType()
		{
			return mediaType;
		}

		public void setMediaType(String mediaType)
		{
			this.mediaType = mediaType;
		}

		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}
	}

	@Override
	protected List<Attachment> createAttachments(SectionInfo info)
	{
		List<Attachment> attachments = Lists.newArrayList();
		List<String> entries = Collections.emptyList();

		String restriction = kalturaSettings.getRestriction();
		KalturaOption choiceOption = null;

		if( restriction != null )
		{
			choiceOption = KalturaOption.valueOf(restriction);
		}
		else
		{
			choiceOption = choice.getSelectedValue(info);
		}

		switch( choiceOption )
		{
			case EXISTING:
				entries = Lists.newArrayList(results.getSelectedValuesAsStrings(info));
				break;
			case UPLOAD:
				entries = Lists.newArrayList(selections.getSelectedValuesAsStrings(info));
				break;
		}
		for( String entryId : entries )
		{
			attachments.add(createAttachment(entryId));
		}
		return attachments;
	}

	private Attachment createAttachment(String entryId)
	{
		MediaEntry entry = kalturaService
				.getMediaEntry(getKalturaClient(getKalturaServer(), SessionType.ADMIN), entryId);

		CustomAttachment attachment = new CustomAttachment();

		attachment.setType(KalturaUtils.ATTACHMENT_TYPE);
		attachment.setDescription(entry.getName()); // Title

		attachment.setData(KalturaUtils.PROPERTY_KALTURA_SERVER, kalturaSettings.getServerUuid());
		attachment.setData(KalturaUtils.PROPERTY_DESCRIPTION, entry.getDescription());
		attachment.setData(KalturaUtils.PROPERTY_DATE, entry.getCreatedAt() * 1000L);
		String thumbnailUrl = entry.getThumbnailUrl();
		attachment.setData(KalturaUtils.PROPERTY_THUMB_URL, thumbnailUrl);
		attachment.setThumbnail(thumbnailUrl);
		attachment.setData(KalturaUtils.PROPERTY_ENTRY_ID, entry.getId());
		attachment.setData(KalturaUtils.PROPERTY_TITLE, entry.getName());
		attachment.setData(KalturaUtils.PROPERTY_DURATION, (long) entry.getDuration());
		attachment.setData(KalturaUtils.PROPERTY_TAGS, entry.getTags());

		return attachment;
	}

	@Override
	protected void setupDetailEditing(SectionInfo info)
	{
		super.setupDetailEditing(info);
		Attachment attachment = getDetailsAttachment(info);
		String playerId = (String) attachment.getData(KalturaUtils.PROPERTY_CUSTOM_PLAYER);
		players.setSelectedStringValue(info, playerId != null ? playerId : "");
	}

	@Override
	protected void saveDetailsToAttachment(SectionInfo info, Attachment attachment)
	{
		super.saveDetailsToAttachment(info, attachment);
		UiConf conf = players.getSelectedValue(info);
		if( conf != null )
		{
			attachment.setData(KalturaUtils.PROPERTY_CUSTOM_PLAYER, Integer.toString(conf.getId()));
		}
		else
		{
			attachment.getDataAttributes().remove(KalturaUtils.PROPERTY_CUSTOM_PLAYER);
		}
	}

	@Override
	public void cancelled(SectionInfo info)
	{
		super.cancelled(info);
		KalturaHandlerModel model = getModel(info);
		choice.setSelectedStringValue(info, null);
		model.setFinishedUploading(false);
		model.setUploads(new ArrayList<KalturaUploadInfo>());
	}

	@Override
	public AttachmentHandlerLabel getLabel()
	{
		return new AttachmentHandlerLabel(NAME_LABEL, DESCRIPTION_LABEL);
	}

	@Override
	public boolean supports(IAttachment attachment)
	{
		if( attachment instanceof CustomAttachment )
		{
			CustomAttachment ca = (CustomAttachment) attachment;
			return KalturaUtils.ATTACHMENT_TYPE.equals(ca.getType());
		}
		return false;
	}

	@Override
	public String getHandlerId()
	{
		return "kalturaHandler";
	}

	@Override
	public Label getTitleLabel(RenderContext context, boolean editing)
	{
		return editing ? EDIT_TITLE_LABEL : ADD_TITLE_LABEL;
	}

	@Override
	public Class<KalturaHandlerModel> getModelClass()
	{
		return KalturaHandlerModel.class;
	}

	public static class KalturaHandlerModel extends AbstractDetailsAttachmentHandler.AbstractAttachmentHandlerModel
	{
		@Bookmarked
		private List<KalturaUploadInfo> uploads = Lists.newArrayList();
		@Bookmarked
		private boolean finishedUploading;
		private boolean showPlayers;
		private boolean searchPerformed;
		private SectionRenderable kalturaLogo;
		private Label kalturaServer;
		private String playerId;
		private String viewerUrl;

		public List<KalturaUploadInfo> getUploads()
		{
			return uploads;
		}

		public void addUploads(List<KalturaUploadInfo> uploads)
		{
			for( KalturaUploadInfo ul : uploads )
			{
				this.uploads.add(ul);
			}
		}

		public void setShowPlayers(boolean show)
		{
			this.showPlayers = show;
		}

		public boolean isShowPlayers()
		{
			return showPlayers;
		}

		public boolean isSearchPerformed()
		{
			return searchPerformed;
		}

		public void setSearchPerformed(boolean searchPerformed)
		{
			this.searchPerformed = searchPerformed;
		}

		public void setFinishedUploading(boolean finishedUploading)
		{
			this.finishedUploading = finishedUploading;
		}

		public void setUploads(List<KalturaUploadInfo> uploads)
		{
			this.uploads = uploads;
		}

		public SectionRenderable getKalturaLogo()
		{
			return kalturaLogo;
		}

		public void setKalturaLogo(SectionRenderable kalturaLogo)
		{
			this.kalturaLogo = kalturaLogo;
		}

		public Label getKalturaServer()
		{
			return kalturaServer;
		}

		public void setKalturaServer(Label kalturaServer)
		{
			this.kalturaServer = kalturaServer;
		}

		public String getPlayerId() {
			return playerId;
		}

		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}

		public String getViewerUrl() {
			return viewerUrl;
		}

		public void setViewerUrl(String viewerUrl) {
			this.viewerUrl = viewerUrl;
		}
	}

	public static class KalturaResultOption extends VoidKeyOption
	{
		private SectionRenderable thumbnail;
		private SectionRenderable date;
		private SectionRenderable link;
		private Label description;
		private Label author;
		private Label views;

		public KalturaResultOption(String videoId)
		{
			super(null, videoId);
		}

		public void setThumbnail(SectionRenderable thumbnail)
		{
			this.thumbnail = thumbnail;
		}

		public void setAuthor(Label author)
		{
			this.author = author;
		}

		public void setViews(Label views)
		{
			this.views = views;
		}

		public void setDescription(Label description)
		{
			this.description = description;
		}

		public void setDate(SectionRenderable date)
		{
			this.date = date;
		}

		public SectionRenderable getThumbnail()
		{
			return thumbnail;
		}

		public Label getAuthor()
		{
			return author;
		}

		public Label getViews()
		{
			return views;
		}

		public Label getDescription()
		{
			return description;
		}

		public SectionRenderable getDate()
		{
			return date;
		}

		public SectionRenderable getLink()
		{
			return link;
		}

		public void setLink(SectionRenderable link)
		{
			this.link = link;
		}
	}

	public static class KalturaUpload
	{
		private SectionRenderable title;
		private Label description;
		private Label tags;
		private final String videoId;

		public KalturaUpload(String videoId)
		{
			this.videoId = videoId;
		}

		public Label getDescription()
		{
			return description;
		}

		public void setDescription(Label description)
		{
			this.description = description;
		}

		public SectionRenderable getTitle()
		{
			return title;
		}

		public void setTitle(SectionRenderable title)
		{
			this.title = title;
		}

		public Label getTags()
		{
			return tags;
		}

		public void setTags(Label tags)
		{
			this.tags = tags;
		}

		public String getVideoId()
		{
			return videoId;
		}
	}

	public SingleSelectionList<KalturaOption> getChoice()
	{
		return choice;
	}

	public TextField getQuery()
	{
		return query;
	}

	public Button getSearch()
	{
		return search;
	}

	public MultiSelectionList<Void> getResults()
	{
		return results;
	}

	public Pager getPager()
	{
		return pager;
	}

	public Div getDivKcw()
	{
		return divKcw;
	}

	public MultiSelectionList<KalturaUpload> getSelections()
	{
		return selections;
	}

	public Link getSelectAll()
	{
		return selectAll;
	}

	public Link getSelectNone()
	{
		return selectNone;
	}

	public Div getDivKdp()
	{
		return divKdp;
	}

	@Override
	protected boolean validateAddPage(SectionInfo info)
	{
		return true;
	}

	public SingleSelectionList<UiConf> getPlayers()
	{
		return players;
	}

	@Override
	public String getMimeType(SectionInfo info)
	{
		return KalturaUtils.MIME_TYPE;
	}
}