#import "WorldfunclublocalPlugin.h"
#if __has_include(<worldfunclublocal/worldfunclublocal-Swift.h>)
#import <worldfunclublocal/worldfunclublocal-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "worldfunclublocal-Swift.h"
#endif

@implementation WorldfunclublocalPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftWorldfunclublocalPlugin registerWithRegistrar:registrar];
}
@end
