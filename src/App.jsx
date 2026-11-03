import React, { useState } from 'react';
import {
  Sparkles,
  BookOpen,
  HeartPulse,
  Clock,
  Scale,
  Users,
  FileBarChart2,
  Moon,
  Sun,
  ShieldCheck,
  CheckCircle2,
  Compass,
  Info
} from 'lucide-react';
import QuranMemorization from './components/QuranMemorization';
import DeepUnderstanding from './components/DeepUnderstanding';
import MuslimProductivity from './components/MuslimProductivity';
import FinancialTools from './components/FinancialTools';
import CommunityAndKids from './components/CommunityAndKids';
import FeasibilityReport from './components/FeasibilityReport';

export default function App() {
  const [activeModule, setActiveModule] = useState('report');

  const navigationItems = [
    { id: 'report', name: 'دراسة جدوى الميزات', icon: FileBarChart2, badge: 'تقييم تقني ودراسة شاملة' },
    { id: 'memorization', name: 'الحفظ والمراجعة الذكية', icon: BookOpen, badge: 'تسميع بالذكاء الاصطناعي' },
    { id: 'deepUnderstanding', name: 'المعايشة والفهم العميق', icon: HeartPulse, badge: 'الباحث النفسي والسياقي' },
    { id: 'productivity', name: 'إنتاجية المسلم', icon: Clock, badge: 'منظم الصلاة والوضع الصامت' },
    { id: 'financial', name: 'المعاملات المالية والشرعية', icon: Scale, badge: 'ماسح العقود والزكاة' },
    { id: 'communityKids', name: 'المجتمع والجيل الجديد', icon: Users, badge: 'مساجد وختمات وقصص' },
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans dir-rtl selection:bg-emerald-500 selection:text-white">
      {/* TOP HEADER / NAVBAR */}
      <header className="sticky top-0 z-50 bg-slate-900/90 backdrop-blur-md border-b border-slate-800 shadow-xl">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-emerald-600 via-teal-500 to-emerald-400 flex items-center justify-center text-white shadow-xl shadow-emerald-900/40 font-serif font-black text-2xl border border-emerald-300/40 transform hover:rotate-3 transition-transform">
              أ
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl md:text-2xl font-black text-white tracking-wide">تطبيق أثَر</h1>
                <span className="bg-emerald-500/20 text-emerald-300 text-[10px] font-extrabold px-2.5 py-0.5 rounded-full border border-emerald-500/30">
                  ATHAR AI v1.0
                </span>
              </div>
              <p className="text-xs text-slate-400 hidden sm:block">التطبيق الإسلامي التفاعلي القائم على الذكاء الاصطناعي وتنظيم الإنتاجية</p>
            </div>
          </div>

          <div className="hidden lg:flex items-center gap-3 bg-slate-950/80 px-4 py-2 rounded-2xl border border-slate-800 text-xs">
            <span className="flex items-center gap-1.5 text-slate-300">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></span>
              أوقات الصلوات اليوم:
            </span>
            <span className="text-emerald-400 font-bold">الفجر 04:45 ص</span>
            <span className="text-slate-600">|</span>
            <span className="text-emerald-400 font-bold">الظهر 12:15 م</span>
            <span className="text-slate-600">|</span>
            <span className="text-emerald-400 font-bold">المغرب 06:10 م</span>
          </div>
        </div>
      </header>

      {/* NAVIGATION TABS */}
      <div className="bg-slate-900/60 border-b border-slate-800/80 sticky top-20 z-40 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <nav className="flex items-center gap-2 overflow-x-auto py-3 no-scrollbar">
            {navigationItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeModule === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveModule(item.id)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs md:text-sm font-bold transition-all whitespace-nowrap shrink-0 ${
                    isActive
                      ? 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white shadow-lg shadow-emerald-950 border border-emerald-400/40'
                      : 'bg-slate-950/60 hover:bg-slate-800 text-slate-400 hover:text-slate-200 border border-slate-800/80'
                  }`}
                >
                  <Icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                  <span>{item.name}</span>
                </button>
              );
            })}
          </nav>
        </div>
      </div>

      {/* MAIN CONTENT AREA */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeModule === 'report' && <FeasibilityReport />}
        {activeModule === 'memorization' && <QuranMemorization />}
        {activeModule === 'deepUnderstanding' && <DeepUnderstanding />}
        {activeModule === 'productivity' && <MuslimProductivity />}
        {activeModule === 'financial' && <FinancialTools />}
        {activeModule === 'communityKids' && <CommunityAndKids />}
      </main>

      {/* FOOTER */}
      <footer className="bg-slate-900 border-t border-slate-800 py-8 text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
            <p className="text-slate-400">
              تطبيق <strong className="text-emerald-400 font-bold">أثَر (Athar)</strong> - رؤية هندسية حديثة للتطبيقات الإسلامية التفاعلية
            </p>
          </div>
          <div className="flex items-center gap-4 text-slate-400">
            <button onClick={() => setActiveModule('report')} className="hover:text-emerald-400 transition-colors">
              عرض دراسة الجدوى والتقييم الفني
            </button>
            <span>•</span>
            <span>الذكاء الاصطناعي والإنتاجية</span>
            <span>•</span>
            <span>جميع الحقوق محفوظة © {new Date().getFullYear()}</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
