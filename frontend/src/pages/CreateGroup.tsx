import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Users, Save } from 'lucide-react';
import { groupsAPI } from '../services/api';
import type { CreateGroupRequest } from '../types';

const CreateGroup: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<CreateGroupRequest>({
    name: '',
    description: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      setError('Grup adı gereklidir');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const newGroup = await groupsAPI.createGroup(formData);
      // Navigate to the new group's detail page
      navigate(`/groups/${newGroup.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Grup oluşturulurken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="h-5 w-5 text-gray-600" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Yeni Grup Oluştur</h1>
            <p className="text-gray-600 mt-1">
              Arkadaşlarınızla giderlerinizi paylaşmak için bir grup oluşturun
            </p>
          </div>
        </div>
      </div>

      {/* Form */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
              <p className="text-red-700">{error}</p>
            </div>
          )}

          <div>
            <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
              Grup Adı *
            </label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              placeholder="Örn: Ev Arkadaşları, Tatil Grubu"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
              Açıklama (İsteğe bağlı)
            </label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Bu grup hakkında kısa bir açıklama yazın..."
              rows={4}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div className="flex items-start">
              <Users className="h-5 w-5 text-blue-600 mr-2 mt-0.5" />
              <div>
                <h3 className="text-sm font-medium text-blue-900">Grup Oluşturulduktan Sonra</h3>
                <p className="text-sm text-blue-700 mt-1">
                  Grubu oluşturduktan sonra arkadaşlarınızı e-posta adresleriyle davet edebilirsiniz.
                  Grup detay sayfasından üye ekleyebilir ve harcama eklemeye başlayabilirsiniz.
                </p>
              </div>
            </div>
          </div>

          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="flex-1 btn btn-secondary"
              disabled={loading}
            >
              İptal
            </button>
            <button
              type="submit"
              className="flex-1 btn btn-primary flex items-center justify-center gap-2"
              disabled={loading}
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                  Oluşturuluyor...
                </>
              ) : (
                <>
                  <Save className="h-5 w-5" />
                  Grup Oluştur
                </>
              )}
            </button>
          </div>
        </form>
      </div>

      {/* Help Section */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Nasıl Çalışır?
        </h3>
        <div className="space-y-3">
          <div className="flex items-start">
            <div className="bg-primary-100 text-primary-600 rounded-full w-6 h-6 flex items-center justify-center text-sm font-bold mr-3 mt-0.5">
              1
            </div>
            <div>
              <h4 className="font-medium text-gray-900">Grup Oluştur</h4>
              <p className="text-sm text-gray-600">
                Anlamlı bir grup adı ve açıklama ile başlayın
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <div className="bg-primary-100 text-primary-600 rounded-full w-6 h-6 flex items-center justify-center text-sm font-bold mr-3 mt-0.5">
              2
            </div>
            <div>
              <h4 className="font-medium text-gray-900">Arkadaş Davet Et</h4>
              <p className="text-sm text-gray-600">
                E-posta adresleriyle arkadaşlarınızı gruba davet edin
              </p>
            </div>
          </div>
          <div className="flex items-start">
            <div className="bg-primary-100 text-primary-600 rounded-full w-6 h-6 flex items-center justify-center text-sm font-bold mr-3 mt-0.5">
              3
            </div>
            <div>
              <h4 className="font-medium text-gray-900">Harcama Ekle</h4>
              <p className="text-sm text-gray-600">
                Ortak harcamalarınızı ekleyin ve paylaşın
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateGroup;