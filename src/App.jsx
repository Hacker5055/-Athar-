import React, { useState } from 'react';
import { Sparkles, BookOpen, HeartPulse, Clock, Scale, Users, FileBarChart2, Moon, Sun, ShieldCheck } from 'lucide-react';
import QuranMemorization from './components/QuranMemorization';
import DeepUnderstanding from './components/DeepUnderstanding';
import MuslimProductivity from './components/MuslimProductivity';
import FinancialTools from './components/FinancialTools';
import CommunityAndKids from './components/CommunityAndKids';
import FeasibilityReport from './components/FeasibilityReport';

export default function App() {
  const [activeModule, setActiveModule] = useState('memorization');

  const navigationItems = [
    { id: 'memorization', name: 'الحفظ والمراجعة الذكية', icon: BookOpen, badge: 'تسميع بالذكاء الاصطناعي' },
    { id: 'deepUnderstanding', name: 'المعايشة والفهم العميق', icon: HeartPulse, badge: 'الباحث النفسي والسياقي' },
    { id: 'productivity', name: 'إنتاجية المسلم', icon: Clock, badge: 'منظم الصلاة والوضع الصامت' },
    { id: 'financial', name: 'المعاملات المالية والشرعية', icon: Scale, badge: 'ماسح العقود والزكاة' },
    { id: 'communityKids', name: 'المجتمع والجيل الجديد', icon: Users, badge: 'مساجد وختمات وقصص' },
    { id: 'report', name: 'دراسة جدوى الميزات', icon: FileBarChart2, badge: 'تقييم تقني شامل' },
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans dir-rtl">
      {/* TOP HEADER / NAVBAR */}
      <header className="sticky top-0 z-50 bg-slate-900/90 backdrop-blur-md border-b border-slate-800/80 shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-2xl bg-gradient-to-tr from-emerald-600 to-teal-500 flex items-center justify-center text-white shadow-lg shadow-emerald-600/30 font-serif font-black text-2xl border border-emerald-400/40">
              أ
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-extrabold text-white tracking-wide">تطبيق أثَر</h1>
                <span className="bg-emerald-500/20 text-emerald-300 text-[10px] font-bold px-2 py-0.5 rounded-full border border-emerald-500/30">
                  ATHAR APP v1.0
                </span>
              </div>
              <p className="text-xs text-slate-400">التجربة الإسلامية الحديثة بالذكاء الاصطناعي والإنتاجية</p>
            </div>
          </div>

          <div className="hidden md:flex items-center gap-3 bg-slate-950/80 px-4 py-2 rounded-xl border border-slate-800 text-xs">
            <span className="flex items-center gap-1.5 text-slate-300">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping"></span>
              أوقات الصلاة اليوم:
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
      <div className="bg-slate-900/60 border-b border-slate-800/60">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <nav className="flex items-center gap-2 overflow-x-auto py-3 no-scrollbar">
            {navigationItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeModule === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveModule(item.id)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs md:text-sm font-semibold transition-all whitespace-nowrap shrink-0 ${
                    isActive
                      ? 'bg-gradient-to-r from-emerald-600 to-teal-600 text-white shadow-lg shadow-emerald-900/30 border border-emerald-400/40'
                      : 'bg-slate-950/50 hover:bg-slate-800/80 text-slate-400 hover:text-slate-200 border border-slate-800'
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
        {activeModule === 'memorization' && <QuranMemorization />}
        {activeModule === 'deepUnderstanding' && <DeepUnderstanding />}
        {activeModule === 'productivity' && <MuslimProductivity />}
        {activeModule === 'financial' && <FinancialTools />}
        {activeModule === 'communityKids' && <CommunityAndKids />}
        {activeModule === 'report' && <FeasibilityReport />}
      </main>

      {/* FOOTER */}
      <footer className="bg-slate-900 border-t border-slate-800 py-6 text-xs text-slate-500 text-center">
        <div className="max-w-7xl mx-auto px-4 flex flex-col md:flex-row items-center justify-between gap-3">
          <p className="text-slate-400">
            تطبيق <span className="text-emerald-400 font-bold">أثَر (Athar)</span> - جميع الحقوق محفوظة © {new Date().getFullYear()}
          </p>
          <div className="flex items-center gap-4 text-slate-400">
            <span>الابتكار الإسلامي والذكاء الاصطناعي</span>
            <span>•</span>
            <button onClick={() => setActiveModule('report')} className="hover:text-emerald-400 underline">
              عرض دراسة وتقييم الجدوى الفنية
            </button>
          </div>
        </div>
      </footer>
    </div>
  );
}
